package td2;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class PhilosopherDinner {
    private final ReentrantLock[] forks;

    public PhilosopherDinner(int forkCount) {
        ReentrantLock[] forks = new ReentrantLock[forkCount];
        Arrays.setAll(forks, i -> new ReentrantLock());
        this.forks = forks;
    }

    public void eat(int index) {
        ReentrantLock fork1 = forks[index];
        ReentrantLock fork2 = forks[(index + 1) % forks.length];
        fork1.lock();
        try {
            if(fork2.tryLock(1, TimeUnit.MILLISECONDS)) {
                try {
                    System.out.println("philosopher " + index + " eat");
                } finally {
                    fork2.unlock();
                }
            }
        } catch (InterruptedException e) {} finally {
            fork1.unlock();
        }
    }

    public static void main(String[] args) {
        PhilosopherDinner dinner = new PhilosopherDinner(5);
        IntStream.range(0, 5).forEach(i -> {
            new Thread(() -> {
                for(;;) {
                    dinner.eat(i);
                }
            }).start();
        });
    }
}