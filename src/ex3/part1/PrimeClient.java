package ex3.part1;

import ex2.part4.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Map;

public class PrimeClient{
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 1235;
    private static final long INITIAL_VALUE = (long) 1e17;
    private static final long COUNT = 40;
    private static final String CLIENT_NAME = PrimeClient.class.getName();

    private Component communication;
    private String hostname;
    private int port;
    private int  portin;
    private boolean synchronisiert = true;
    private static boolean nebenlaeufig = true;
    private long initialValue, count;
    private long communicationTimeStart;

    private TimeCounter timeCounter = new TimeCounter();;

    public PrimeClient(String hostname, int port, boolean synchronisiert, long initialValue, long count) {
        this.portin = (int) (Math.random() * (3000 - 2000) + 2000);
        this.hostname = hostname;
        this.port = port;
        this.synchronisiert = synchronisiert;
        this.initialValue = initialValue;
        this.count = count;
    }

    public void run() throws ClassNotFoundException, IOException {
        communication = new Component();
        if (synchronisiert == true) {
            if (nebenlaeufig == true) {
                System.out.println("blockierend + nebenläufig");
                for (long i = initialValue; i < initialValue + count; i++)
                    processNumberNeben(i);
            } else {
                System.out.println("synchro");
                for (long i = initialValue; i < initialValue + count; i++)
                    processNumber(i);
            }
        } else {
            System.out.println("nicht synchro");
            for (long i = initialValue; i < initialValue + count; i++)
                processNumberNotSync(i);
        }
    }

    public void processNumber(long value) throws IOException, ClassNotFoundException {
        communicationTimeStart = System.currentTimeMillis();
        communication.send(new Message(hostname, portin, new Long(value)), portin, false);
        System.out.println(value + ": ");
        Map<String, String> receivedData = (Map<String, String>) communication.receive(portin, true, true).getContent();
        System.out.println(generateOutputMessage(receivedData));
    }

    public void processNumberNotSync(long value) throws IOException, ClassNotFoundException {
        communicationTimeStart = System.currentTimeMillis();
        System.out.print(value + ": ");
        communication.send(new Message(hostname, portin, new Long(value)), port, false);
        Message antwort = null;
        do {
            antwort = communication.receive(portin, false, true);
            System.out.print(".");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (antwort == null);

        Map<String, String> receivedData = (Map<String, String>) antwort.getContent();
        System.out.println(generateOutputMessage(receivedData));
    }

    public void processNumberNeben(long value) throws IOException, ClassNotFoundException {
        communicationTimeStart = System.currentTimeMillis();
        System.out.print(value + ": ");
        communication.send(new Message(hostname, portin, new Long(value)), port, false);
        Thread b = new NebenThread();
        b.start();
        Map<String, String> receivedData = (Map<String, String>) communication.receive(portin, true, true).getContent();
        System.out.println(generateOutputMessage(receivedData));
        b.stop();
    }

    // ausgabe der Zeit
    public String generateOutputMessage(Map<String, String> receivedData) {

        // holt Schlüsselwert
        long p = Long.parseLong(receivedData.get("p"));
        long w = Long.parseLong(receivedData.get("w"));
        long c = (System.currentTimeMillis() - communicationTimeStart) - p - w;

        this.timeCounter.addCommunicationTime(c);
        this.timeCounter.addProcessingTime(p);
        this.timeCounter.addWaitingTime(w);

        //Methode in TimeCounter die alle werte holt
        Map<String, Long> avgs = this.timeCounter.getAverages();

        //holt den Schlüssel zu p w c
        long pAvg = avgs.get("pt");
        long wAvg = avgs.get("wt");
        long cAvg = avgs.get("ct");

        String outputMsg = "\n"+
                "p: " + p + "ms | (" + pAvg + ") ms +\n" +
                            "w: " + w + "ms | (" + wAvg + ") ms \n" +
                            "c: " + c + "ms | (" + cAvg + ") ms \n" +
                           "isprime: " + receivedData.get("isprime") + "\n";

        return outputMsg;
    }

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        String hostname = HOSTNAME;
        int port = PORT;
        boolean synchronisiert = true;
        long initialValue = INITIAL_VALUE;
        long count = COUNT;
        boolean doExit = false;
        String input;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Welcome to " + CLIENT_NAME + "\n");

        while (!doExit) {
            System.out.print("Server hostname [" + hostname + "] > ");
            input = reader.readLine();
            if (!input.equals(""))
                hostname = input;

            System.out.print("Server port [" + port + "] > ");
            input = reader.readLine();
            if (!input.equals(""))
                port = Integer.parseInt(input);

            System.out.print("Request Mode [SYNCHRONIZED] > ");
            input = reader.readLine();
            if (input.equals("n"))
                synchronisiert = false;

            if (synchronisiert == true) {
                System.out.println("Request Mode [nebenläufig?] > ");
                input = reader.readLine();
                if (input.equals("n"))
                    nebenlaeufig = false;
            }

            System.out.print("Prime search initial value [" + initialValue
                    + "] > ");
            input = reader.readLine();
            if (!input.equals(""))
                initialValue = Integer.parseInt(input);

            System.out.print("Prime search count [" + count + "] > ");
            input = reader.readLine();
            if (!input.equals(""))
                count = Integer.parseInt(input);

            new PrimeClient(hostname, port, synchronisiert, initialValue, count)
                    .run();

            System.out.println("Exit [n]> ");
            input = reader.readLine();
            if (input.equals("y") || input.equals("j"))
                doExit = true;
        }
    }

    public class NebenThread extends Thread {
        public void run() {
            do {
                System.out.print(".");
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

}
