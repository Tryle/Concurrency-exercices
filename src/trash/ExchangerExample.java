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
     * 0: value est vide et doit être remplie par put
     * 1: value est remplie et doit être retournée par release
     * 2: value est remplie et doit être retournée par put
     */
    private int status = 0;

    /*
     * Si le statut status est à l'état 0, on appelle la méthode put, et s'il est à l'état 1, on appelle
     * la méthode release. S'il est à l'état 2, on fait attendre le thread tant que le job n'est pas terminé,
     * c'est-à-dire tant que l'échange entre les deux threads n'a pas été fait.
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
     * On rentre dans cette méthode, cela signifie que le statut est à 0. On peut donc modifier value et y mettre
     * notre objet à échanger, et signifier aux threads en attente dans la méthode exchange qu'ils peuvent se réveiller.
     * Ensuite on met le thread actuel en attente pour qu'un autre thread initie l'échange dans la méthode release.
     *
     * Le thread se réveille une fois que le statut passe à 2 dans la méthode release. On passe alors le statut à 0,
     * on réveille les threads de la méthode exchange et on retourne la value qui a été modifiée par un autre thread
     * dans la méthode release.
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
     * Le statut est à 1. Ici on sait donc que value est remplie, donc on la récupère et on la modifie par element.
     * On change le statut à 2, puis on notifie le thread en attente dans la méthode put qu'il peut se réveiller.
     * On alors retourne la value précédemment récupérée.
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