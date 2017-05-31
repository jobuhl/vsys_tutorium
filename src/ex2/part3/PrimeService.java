package ex2.part3;

import rm.requestResponse.*;

import java.io.IOException;

/**
 * Created by Micha on 29.05.2017.
 */
public class PrimeService extends Thread {
    private long number;
    private Component communication;
    private int port;
    private Boolean isPrime;
    private ThreadCounter counter;

    public PrimeService(long number, int port, Component communication){
        this.number = number;
        this.port = port;
        this.communication = communication;
    }

    public void run() {
        isPrime = primeService();
        try {
            communication.send(new Message("localhost", port, isPrime), port, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        synchronized (PrimeServer.obj) {
            PrimeServer.obj.notify();
            PrimeServer.counter.decrement();
        }
    }

    public boolean primeService() {
        for (long i = 2; i < Math.sqrt(number)+1; i++) {
//            System.out.println(Thread.currentThread().getName() + " : " + i);
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }
}
