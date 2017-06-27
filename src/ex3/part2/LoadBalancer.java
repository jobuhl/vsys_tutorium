package ex3.part2;
import ex2.part4.*;
import rm.serverAdmin.ServerAdmin;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class LoadBalancer {
    public static final int LISTENING_TO_PORT = 1235;
    public static ServerAdmin serverAdmin;
    public static Properties properties = new Properties();
    public static Component communication;
    public static int port;
    public static ThreadPoolExecutor executor;
    public static int poolSize = 5;

    public LoadBalancer(int port) {
        this.port = port;
        this.communication = new Component();
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(this.poolSize);
    }

    public static void main(String[] args) {
        try {
            serverAdmin = new ServerAdmin("/Users/robert/FH/vsys/Aufgabenblatt_3/src/part2/config.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        new LoadBalancer(LISTENING_TO_PORT).listen();
    }

    private static void listen() {
        while(true) {
            try {
                Message message = communication.receive(port, true, false);
                Runnable runnable = new Messenger(message, serverAdmin, communication);
                executor.execute(runnable);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
