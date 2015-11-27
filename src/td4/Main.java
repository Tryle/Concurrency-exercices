package td4;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class SynchronizedBlockingBuffer<T> {
    private final Object[] arr;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    private int size;

    public SynchronizedBlockingBuffer(int capacity) {
        if(capacity < 1) {
            throw new IllegalArgumentException("capacity must be strictly posivite.");
        }
        arr = new Object[capacity];
    }

    @SuppressWarnings("unchecked")
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while(size == 0) {
                notEmpty.await();
            }
            return (T) arr[--size];
        } finally {
            notFull.signal();
            lock.unlock();
        }
    }

    public void add(T o) throws InterruptedException {
        Objects.requireNonNull(o);
        lock.lock();
        try {
            while(size == arr.length) {
                notFull.await();
            }
            arr[size++] = o;
        } finally {
            notEmpty.signal();
            lock.unlock();
        }

    }
}

public class Main {
    private final SynchronizedBlockingBuffer<String> queue = new SynchronizedBlockingBuffer<>(100);

    public Main() {
        Map<String, Integer> paramsThread = new HashMap<>();
        paramsThread.put("hello 0", 4);
        paramsThread.put("hello 1", 3);

        for(Map.Entry<String, Integer> entry: paramsThread.entrySet()) {
            new Thread(runnableInsert(entry.getKey(), entry.getValue())).start();
        }

        new Thread(() -> {
            for(;;) {
                try {
                    System.out.println(queue.take());
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                }
            }
        }).start();

    }

    private Runnable runnableInsert(String str, int delay) {
        return () -> {
            for(;;) {
                try {
                    queue.add(str);
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                }
            }
        };
    }

    public static void main(String[] args) {
        new Main();
    }
}
