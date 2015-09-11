package td1;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class HelloThreadSum2 {
    public static void main(String[] args) throws InterruptedException {
        AtomicInteger result = new AtomicInteger(0);
        List<Thread> threads = new LinkedList<>();
        IntStream.range(0, 5).forEach(e -> {
            threads.add(new Thread(() -> {
                int sum = 0;
                int idx = e * 1000 + 1;
                while (!Thread.currentThread().isInterrupted()) {
                    if (idx <= (e + 1) * 1000) {
                        sum += idx++;
                    } else {
                        result.addAndGet(sum);
                        Thread.currentThread().interrupt();
                    }
                }
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
