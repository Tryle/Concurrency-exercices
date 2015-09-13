package td3;

import java.util.stream.IntStream;

class Exchanger<T> {
    private final static Object LOCK = new Object();
    private T value;
    private int count = 0;
    public T exchange(T element) throws InterruptedException {
        synchronized(LOCK) {
            count++;
            if(count % 2 == 1) {
                value = element;
                LOCK.wait();
                return value;
            } else {
                T result = value;
                value = element;
                LOCK.notify();
                return result;
            }
        }
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