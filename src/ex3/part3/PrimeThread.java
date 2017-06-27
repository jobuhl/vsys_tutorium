package ex3.part3;

import java.util.logging.Logger;

/**
 * Created by robert on 28.06.16.
 */
public class PrimeThread extends Thread{
    private Counter counter;
    private long number;
    boolean result;
    private boolean done;
    private final static Logger logger = Logger.getLogger(RMIPrimeServer.class.getName());

    public PrimeThread(long number, Counter counter){
        this.number = number;
        this.counter = counter;
        this.done = false;
        counter.incrementCount();
    }

    public final void run(){
        try {
            result = this.primeService(number);
            Thread.sleep(500);
            this.done = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isDone(){
        return this.done;
    }

    public boolean getResult(){
        return result;
    }


    private boolean primeService(long number) {
        for (long i = 2; i < Math.sqrt(number) + 1; i++) {
            if (number % i == 0)
                return false;
        }
        return true;
    }
}
