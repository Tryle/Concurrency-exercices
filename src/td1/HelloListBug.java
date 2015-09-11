package td1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class HelloListBug {
    private final List<Thread> threads;
    private final List<Integer> elements;
    private final int number;
    public HelloListBug(int number) {
        this.number = number;
        this.elements = Collections.synchronizedList(new ArrayList<>(5000 * number));
        threads = new LinkedList<>();
        IntStream.range(0, number).forEach(i -> {
            threads.add(new Thread(() -> {
                IntStream.range(0, 5000).forEach(elements::add);
            }));
        });
    }

    public void start() {
        threads.forEach(Thread::start);
    }

    public void join() throws InterruptedException {
        for(Thread t: threads) {
            t.join();
        }
    }

    public int getElementsSize() {
        return elements.size();
    }

    public static void main(String[] args) throws InterruptedException {
        HelloListBug helloThread = new HelloListBug(4);
        helloThread.start();
        helloThread.join();
        System.out.println("End: " + helloThread.getElementsSize() + ".");
    }
}
