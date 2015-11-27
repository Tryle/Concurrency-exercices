package trash;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

class Exchanger<T> {
    private static final boolean LOG = false;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition jobDone = lock.newCondition();

    private T value;

    /*
     * 0: value est vide et doit �tre remplie par put
     * 1: value est remplie et doit �tre retourn�e par release
     * 2: value est remplie et doit �tre retourn�e par put
     */
    private int status = 0;

    /*
     * Si le statut status est � l'�tat 0, on appelle la m�thode put, et s'il est � l'�tat 1, on appelle
     * la m�thode release. S'il est � l'�tat 2, on fait attendre le thread tant que le job n'est pas termin�,
     * c'est-�-dire tant que l'�change entre les deux threads n'a pas �t� fait.
     */
    public T exchange(T element) throws InterruptedException {
        lock.lock();
        try {
            for(;;) {
                if(status == 1) {
                    return release(element);
                } else if(status == 0) {
                    return put(element);
                } else {
                    log("thread " + element + " waiting");
                    jobDone.await();
                    log("thread " + element + " not waiting anymore");
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /*
     * On rentre dans cette m�thode, cela signifie que le statut est � 0. On peut donc modifier value et y mettre
     * notre objet � �changer, et signifier aux threads en attente dans la m�thode exchange qu'ils peuvent se r�veiller.
     * Ensuite on met le thread actuel en attente pour qu'un autre thread initie l'�change dans la m�thode release.
     *
     * Le thread se r�veille une fois que le statut passe � 2 dans la m�thode release. On passe alors le statut � 0,
     * on r�veille les threads de la m�thode exchange et on retourne la value qui a �t� modifi�e par un autre thread
     * dans la m�thode release.
     */
    private T put(T element) throws InterruptedException {
        log("begin put " + element);
        jobDone.signalAll();
        value = element;
        status = 1;
        while(status == 1) {
            notFull.await();
        }
        log("put " + element + " returns " + value);
        status = 0;
        jobDone.signalAll();
        return value;
    }

    /*
     * Le statut est � 1. Ici on sait donc que value est remplie, donc on la r�cup�re et on la modifie par element.
     * On change le statut � 2, puis on notifie le thread en attente dans la m�thode put qu'il peut se r�veiller.
     * On alors retourne la value pr�c�demment r�cup�r�e.
     */
    private T release(T element) throws InterruptedException {
        log("begin release " + element);
        T result = value;
        value = element;
        status = 2;
        notFull.signal();
        log("release " + element + " returns " + result);
        return result;
    }

    private void log(String message) {
        if(LOG) {
            System.err.println(message);
        }
    }
}

public class ExchangerExample {
    public static void main(String[] args) throws InterruptedException {

        Exchanger<Integer> exchanger = new Exchanger<>();
        IntStream.range(0, 10).forEach(i -> {
            new Thread(() -> {
                try {
                    System.out.println("thread "+i + " received from thread " + exchanger.exchange(i));
                } catch (InterruptedException e) {
                    throw new AssertionError(e);
                }
            }).start();
        });
    }
}