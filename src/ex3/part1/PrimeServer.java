package ex3.part1;

import ex2.part4.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrimeServer {
    private final static Logger LOGGER = Logger.getLogger(PrimeServer.class.getName());
    private final static int PORT = 1235;

    private Component communication;
    private int port = PORT;
    private Counter counter = new Counter();

    private long waitingTimeStart;
    private long processingTimeStart;
    private long processingTime;
    private long waitingTime;

    ExecutorService threadPool = Executors.newFixedThreadPool(5);


    private class Messager implements Runnable {

        private Message message;

        public Messager(Message message) {
            this.message = message;
            counter.incrementCount();
        }

        @Override
        public final void run() {
            try {
                LOGGER.fine(this.message.toString() + " received.");
                LOGGER.finer("Sending ...");
                String msgToSend = "";

                //calls primeService and counts time which was needed to get done
                waitingTime = System.currentTimeMillis() - waitingTimeStart;

                msgToSend += "waiting time: " +  waitingTime + "ms\n";

                processingTimeStart = System.currentTimeMillis();
                boolean isPrime = primeService((Long) this.message.getContent());
                processingTime = System.currentTimeMillis() - processingTimeStart;

                Map<String, String> messageToSend = new HashMap<String, String>();
                messageToSend.put("isprime", Boolean.toString(isPrime));
                messageToSend.put("p", Long.toString(processingTime));
                messageToSend.put("w", Long.toString(waitingTime));

                communication.send(new Message("localhost", port, messageToSend), this.message.getPort(), true);
                LOGGER.fine("message sent.");
                counter.decrementCount();
            } catch (IOException ex) {
                Logger.getLogger(PrimeServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    PrimeServer(int port) {
        communication = new Component();
        if (port > 0)
            this.port = port;
        setLogLevel(Level.FINER);
        counter.start();
    }

    private boolean primeService(long number) {
        for (long i = 2; i < Math.sqrt(number) + 1; i++) {
            if (number % i == 0)
                return false;
        }
        return true;
    }

    void setLogLevel(Level level) {
        for (Handler h : LOGGER.getLogger("").getHandlers()) h.setLevel(level);
        LOGGER.setLevel(level);
        LOGGER.info("Log level set to " + level);
    }

    void listen() {
        LOGGER.info("Listening on port " + port);
        while (true) {
            LOGGER.finer("Receiving ...");
            try {
                Message request = communication.receive(port, true, false);

                this.waitingTimeStart = System.currentTimeMillis();

                Thread thread = new Thread(new Messager(request));
                threadPool.execute(thread);

            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int port = 0;

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

        new PrimeServer(port).listen();
    }
}
