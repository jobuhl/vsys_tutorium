package ex1;


/**
 * Created by Jojo on 29.03.17.
 * Das Programm startet 10 Threads nacheinander und es wird gezeigt welche Threads zu welchem Durchlauf gestartet werden,
 * da der kritische bereich nicht blockiert ist, können auch meherer Threads gleichzeitig gestartet werden.
 * so kann es sein, das mehere Threads im gleichen run gestartet werden bevor der nächste aufgerufen wird. Die Ausgabe ist
 * Nebenläufig, da die Threads schneller abgearbeitet werden als es die syso ausgeben kann.
 */
public class MyThreadA extends Thread {

    private static final int threadMax = 10;
    private static int runCount = 0;

    public void run() {
        while (runCount++ < 100) {
            System.out.println(runCount + ": " + Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) {

        for (int i = 0; i < threadMax; i++) {
            new MyThreadA().start();

        }
    }
}

