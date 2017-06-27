package ex3.part3;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIPrimeClient {
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 1235;
    private static final long INITIAL_VALUE = (long) 1e17;
    private static final long COUNT = 40;
    private static final String CLIENT_NAME = RMIPrimeClient.class.getName();
    private static RMIPrimeServerInterface server;
    private static boolean nebenlaeufig = true;
    private String hostname;
    private int port;
    private int portin;
    private boolean synchronisiert = true;
    private long initialValue, count;

    public RMIPrimeClient(String hostname, int port, boolean synchronisiert, long initialValue, long count) {
        this.portin = (int) (Math.random() * (3000 - 2000) + 2000);
        this.hostname = hostname;
        this.port = port;
        this.synchronisiert = synchronisiert;
        this.initialValue = initialValue;
        this.count = count;
    }

    public void run() throws ClassNotFoundException, IOException {
        if (synchronisiert) {
            if (nebenlaeufig) {
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

    public void processNumber(long value) throws IOException,
            ClassNotFoundException {
        boolean isPrime = server.primeService(value);
        System.out.println(value + ": " + (isPrime ? "prime" : "not prime"));
    }

    public void processNumberNotSync(long value) throws RemoteException {
        boolean answer = false;
        int index = server.primeServiceAsync(value);
        while(!answer){
            if(server.calculationDone(index)) {
                boolean isPrime = server.isPrimeResult(index);
                System.out.println(value + ": " + (isPrime ? "prime" : "not prime"));
                answer = true;
            }
            else{
                System.out.print(".");
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void processNumberNeben(long value) throws RemoteException {
        Thread delay = new Delay();
        delay.start();
        Boolean isPrime = server.primeService(value);
        delay.stop();
        System.out.println(value + ": " + (isPrime.booleanValue() ? "prime" : "not prime"));
    }

    public static void main(String args[]) throws IOException,
            ClassNotFoundException, NotBoundException {
        String hostname = HOSTNAME;
        int port = PORT;
        boolean synchronisiert = true;
        long initialValue = INITIAL_VALUE;
        long count = COUNT;
        boolean doExit = false;

        Registry registry = LocateRegistry.getRegistry(hostname, port);
        server = (RMIPrimeServerInterface) registry.lookup("PrimeServer");

        String input;
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                System.in));

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
            synchronisiert = !input.equals("n");

            if (synchronisiert) {
                System.out.println("Request Mode [nebenläufig?] > ");
                input = reader.readLine();
                nebenlaeufig = !input.equals("n");
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

            new RMIPrimeClient(hostname, port, synchronisiert, initialValue, count)
                    .run();

            System.out.println("Exit [n]> ");
            input = reader.readLine();
            if (input.equals("y") || input.equals("j"))
                doExit = true;
        }
    }

    public class Delay extends Thread {
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
