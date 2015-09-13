package td3;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Note: this code does several stupid things !
 */
public class StupidRendezVous<V> {
    private V value;
    private final Object lock = new Object();

    public void set(V value) {
        Objects.requireNonNull(value);
        synchronized(lock) {
            this.value = value;
            lock.notify();
        }
    }

    public V get() throws InterruptedException {
        synchronized(lock) {
            while(value == null) {
                lock.wait();
            }
        }
        return value;
    }

    public static void main(String[] args) throws InterruptedException {
        StupidRendezVous<String> rendezVous = new StupidRendezVous<>();
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
            rendezVous.set("hello");
        }).start();

        System.out.println(rendezVous.get());
    }
}