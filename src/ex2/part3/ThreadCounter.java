package ex2.part3;

/**
 * Created by MB_SP on 29.05.2017.
 */
public class ThreadCounter extends Thread {

    private int counter = 0;
    private Object sth;
    protected boolean running = true;

    public ThreadCounter(Object obj) {
        this.sth = obj;
    }

    public void run() {
        while (running) {
            synchronized (sth) {
                try {
                    sth.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void increment() {
        this.counter++;
        System.out.println("THREADS: " + this.counter);
    }

    public void decrement() {
        this.counter--;
        System.out.println("THREADS: " + this.counter);
    }
}
