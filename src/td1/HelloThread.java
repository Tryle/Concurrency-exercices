package td1;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class HelloThread {
    private final List<Thread> threads;
    private final int number;
    public HelloThread(int number) {
        this.number = number;
        threads = new LinkedList<>();
        IntStream.range(0, number).forEach(i -> {
            threads.add(new Thread(() -> {
                IntStream.rangeClosed(0, 5000).forEach(e -> System.out.println("hello " + i + " " + e));
            }));
        });
    }

    public void start() {
        threads.forEach(Thread::start);
    }

    public static void main(String[] args) {
        HelloThread helloThread = new HelloThread(4);
        helloThread.start();
    }
}
