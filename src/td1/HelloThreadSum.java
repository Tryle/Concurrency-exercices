package td1;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class HelloThreadSum {
    public static void main(String[] args) {
        AtomicInteger result = new AtomicInteger(0);
        final Object LOCK = new Object();
        List<Thread> threads = new LinkedList<>();
        IntStream.range(0, 4).forEach(e -> {
            threads.add(new Thread(() -> {
                int sum = 0;
                int idx = 0;
                while (!Thread.interrupted()) {
                    if (++idx <= 5000) {
                        sum += idx;
                    } else {
                        synchronized(LOCK) {
                            if(Thread.interrupted()) {
                                break;
                            }
                            result.set(sum);
                            threads.forEach(Thread::interrupt);
                            System.err.println("sum found (job " + e + ")");
                        }
                    }
                }
                System.err.println("Job " + e + " done.");
            }));
        });
        threads.forEach(Thread::start);
        threads.forEach(e -> {
            try {
                e.join();
            } catch (InterruptedException e1) {}
        });
        System.out.println(result.get());
    }
}
