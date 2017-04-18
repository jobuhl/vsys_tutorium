package ex1.part1;

/**
 * Created by Jojo on 29.03.17.
 * Es muss ein Monitor eingeführt werden damit nur ein Thread arbeiten kann
 * der Monitor wird auf ein Objekt deklariert hier test
 */
public class MyThreadC extends Thread {
    private static final int threadMax = 10;
    private static int runCount = 0;

    private static Object test  = new Object();

    public void run() {

        synchronized (test){
        while (runCount++ < 100) {
            syso();
         }
        }
    }
    public void syso() {

        try {
            Thread.currentThread().sleep(100);
            System.out.println(runCount + ": " + Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < threadMax; i++) {
            new MyThreadC().start();

        }
    }
}
