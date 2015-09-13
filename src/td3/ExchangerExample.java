package td3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

class Exchanger<T> {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    private T value;
    private boolean full = false;

    public T exchange(T element) throws InterruptedException {
        lock.lock();
        try {
            if(full) {
                return release(element);
            } else {
                return put(element);
            }
        } finally {
            lock.unlock();
        }
    }

    private T put(T element) throws InterruptedException {
        while(full) {
            notFull.await();
        }
        value = element;
        full = true;
        notEmpty.signal();
        System.err.println("put " + element + " returns " + value);
        return value;
    }

    private T release(T element) throws InterruptedException {
        while(!full) {
            notEmpty.await();
        }
        full = false;
        T result = value;
        notFull.signal();
        System.err.println("release " + element + " returns " + value);
        return result;
    }
}

public class ExchangerExample {
    public static void main(String[] args) throws InterruptedException {

        Exchanger<String> exchanger = new Exchanger<>();
        IntStream.range(0, 10).forEach(i -> {
            new Thread(() -> {
                try {
                    System.out.println("thread "+i + " received from " + exchanger.exchange("thread "+i));
                } catch (InterruptedException e) {
                    throw new AssertionError(e);
                }
            }).start();
        });
    }
}