package trash;

public class Holder {
    private int value;
    private boolean done;
    private final Object lock = new Object();
    public static void main(String[] args) {
        Holder holder = new Holder();
        new Thread(() -> {
            synchronized(holder.lock) {
                holder.value = 12;
                holder.done = true;
            }
        }).start();

        for(;;) {
            synchronized(holder.lock) {
                if (holder.done) {
                    break;
                }
            }
        }
        synchronized(holder.lock) {
            System.out.println(holder.value);
        }
    }
}
