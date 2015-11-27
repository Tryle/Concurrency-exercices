package td5;

public class Bogus {
  private volatile boolean stop;

  public void runCounter() {
    int localCounter = 0;
    for(;;) {
      if (stop) {
        break;
      }
      localCounter++;
    }
    System.out.println(localCounter);
  }

  public void stop() {
    stop = true;
  }

  public static void main(String[] args) throws InterruptedException {
    Bogus bogus = new Bogus();
    Thread thread = new Thread(bogus::runCounter);
    thread.start();
    Thread.sleep(100);
    bogus.stop();
    thread.join();
  }
}