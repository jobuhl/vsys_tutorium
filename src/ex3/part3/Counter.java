package ex3.part3;

import java.util.logging.Logger;

public class Counter extends Thread{

    private static final Logger LOGGER = Logger.getLogger(RMIPrimeServer.class.getName());
    private static int count;
    private Object monitor = new Object();

    public Counter(){
        this.count = 1;
    }

    @Override
    public void run() {
        while(true){
            synchronized (monitor){
                try {
                    monitor.wait();
                    LOGGER.info("#Threads: " + this.getCount());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void incrementCount(){
        synchronized (monitor){
            this.setCount(getCount()+1);
            monitor.notify();
        }
    }
    public void decrementCount(){
        synchronized (monitor){
            this.setCount(getCount()-1);
            monitor.notify();
        }
    }

    public int getCount() {
        return this.count;
    }

    private void setCount(int count) {
        this.count = count;
    }
}
