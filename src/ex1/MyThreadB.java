package ex1;

/**
 * Created by Jojo on 29.03.17.
 * Das syncronized blockiert zwar den kritischen Bereich definiert aber nicht in welcher Reihenfolge Threads abgearbeitet
 * werden, daher ist es "zufällig" welcher der wartenden Threads ausgeführt wird und somit wird auch die Reihenfolge nicht
 * eingehalten. Da aber die synchonized Methode nur auf ein Objekt syncronisiert wird somit jeder Thread mit sich selber
 * synconiziert. Die synchronized Methode würde Sinn machen, wenn Unterschiedliche Klasse synchronisiert werden.
 */
public class MyThreadB extends Thread {
    private static final int threadMax = 10;
    private static int runCount = 0;

    public void run() {


        while (runCount++ < 100) {
            syso();
        }
    }


    public synchronized void syso() {

        System.out.println(runCount + ": " + Thread.currentThread().getName());

        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {

        for (int i = 0; i < threadMax; i++) {
            new MyThreadB().start();
        }
    }
}