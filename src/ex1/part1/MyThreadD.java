package ex1.part1;

import java.util.Random;

/**
 * Created by Jojo on 30.03.17.
 * Problematik, die berechnung der zufälligen Zahlen 1 und -1 muss jedesmal neu berechnet werden
 * auf statisches Objekt syncronisieren und darauf achten das jeder Thread bei jeder iteration eine neue Zufallszahl
 * ausgibt.
 *
 */
public class MyThreadD extends MyAccount implements Runnable {


    private static final int threadMax = 10;
    private static int runCount = 0;

    private static Object test = new Object();




    public void run() {




            while (runCount++ < 100) {


                    Random a = new Random();
                    int b = a.nextInt(2);
                synchronized (test) {
                    doSomething(b);
                }
            }
        }

    private void doSomething(int b) {

        if (b == 0) {
            b = -1;
        } else {
            b = 1;
        }


        System.out.println(Thread.currentThread().getName() + ": " + getNumber() + " " + " + " + b + " = "
        + " " + setNumber2(getNumber(),b));


    }


    public static void main(String[] args) {

        for (int i = 0; i < threadMax; i++) {
            new Thread(new MyThreadD()).start();
        }
    }
}
