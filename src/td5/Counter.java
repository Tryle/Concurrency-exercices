package td5;

import java.util.concurrent.atomic.AtomicLong;

public class Counter {
    private volatile boolean stop;
    private AtomicLong counter;

    public Counter() {
        counter = new AtomicLong(0);
    }

    public void runCounter() {
        int localCounter = 0;
        while(!stop) {
            counter.incrementAndGet();
            localCounter++;
        }
        System.out.println(localCounter);
    }

    public void stop() {
        stop = true;
    }

    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();
        Runnable runnable = counter::runCounter;
        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);
        thread1.start();
        thread2.start();
        Thread.sleep(100);
        counter.stop();
        thread1.join();
        thread2.join();
        System.out.println("sum: " + counter.counter.get());
    }
}