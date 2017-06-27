package ex3.part3;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RMIPrimeServer implements RMIPrimeServerInterface {
    private final static Logger LOGGER = Logger.getLogger(RMIPrimeServer.class.getName());
    private final static int PORT = 1235;
    private int port = PORT;
    private Counter counter = new Counter();
    private static RMIPrimeServer rmiPrimeServer;

    private static ThreadPoolExecutor executor;
    Map<Integer, PrimeThread> threads = new HashMap<Integer, PrimeThread>();

    RMIPrimeServer() {
        setLogLevel(Level.FINER);
    }

    @Override
    public boolean primeService(long number) throws RemoteException {
        for (long i = 2; i < Math.sqrt(number) + 1; i++) {
            if (number % i == 0)
                return false;
        }
        return true;
    }

    @Override
    public int primeServiceAsync(long number) throws RemoteException {
        PrimeThread checkPrime = new PrimeThread(number, counter);
        executor.execute(checkPrime);
        int index = threads.size()+1;
        this.threads.put(index, checkPrime);
        return index;
    }

    @Override
    public boolean calculationDone(int index) throws RemoteException {
        PrimeThread calculation = threads.get(index);
        return calculation.isDone();
    }

    @Override
    public boolean isPrimeResult(int index) throws RemoteException {
        PrimeThread calculation = threads.get(index);
        return calculation.getResult();
    }

    public void start(int port) throws RemoteException {
        RMIPrimeServerInterface serverStub = (RMIPrimeServerInterface) java.rmi.server.UnicastRemoteObject.exportObject(rmiPrimeServer, 0);
        Registry registry = LocateRegistry.createRegistry(port);
        registry.rebind("PrimeServer", serverStub);
    }

    void setLogLevel(Level level) {
        for (Handler h : LOGGER.getLogger("").getHandlers()) h.setLevel(level);
        LOGGER.setLevel(level);
        LOGGER.info("Log level set to " + level);
    }

    public static void main(String[] args) throws RemoteException {
        rmiPrimeServer = new RMIPrimeServer();
        rmiPrimeServer.start(PORT);
        String input;
        int port = 0;

        System.out.println("Port >");
        Scanner s = new Scanner(System.in);
        port = s.nextInt();
        System.out.println("pool [1 = fixed || 0 = cached] >");
        int pool = s.nextInt();

        if(pool == 1) {
            System.out.println("pool size >");
            executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(s.nextInt());
        } else if(pool == 0) {
            executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        } else {
            System.out.println("enter 0 or 1");
        }

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-port":
                    try {
                        port = Integer.parseInt(args[++i]);
                    } catch (NumberFormatException e) {
                        LOGGER.severe("port must be an integer, not " + args[i]);
                        System.exit(1);
                    }
                    break;
                default:
                    LOGGER.warning("Wrong parameter passed ... '" + args[i] + "'");
            }
        }
    }
}
