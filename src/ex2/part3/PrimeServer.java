package ex2.part3;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import ex2.part4.Component;
import ex2.part4.Message;


public class PrimeServer implements Runnable {
	private final static int PORT = 1234, POOLSIZE = 10;
	private final static Logger LOGGER = Logger.getLogger(PrimeServer.class.getName());
	private Component communication;
	private int port = PORT, requestzaehler, anzahlAnfragen = 20, portcounter = 0;

//	static ExecutorService executor = Executors.newFixedThreadPool(POOLSIZE);
//	static ExecutorService executor = Executors.newCachedThreadPool();
	static ExecutorService executor;


	PrimeServer(int port) {
		communication = new Component();
		if (port > 0)
			this.port = port;
	}

	private boolean primeService(long number) {
		for (long i = 2; i < Math.sqrt(number) + 1; i++) {
			if (number % i == 0)
				return false;
		}
		return true;
	}

	@SuppressWarnings("static-access")
	void setLogLevel(Level level) {
		for (Handler h : LOGGER.getLogger("").getHandlers())
			h.setLevel(level);
		LOGGER.setLevel(level);
		LOGGER.info("Log level set to " + level);
	}

	/**
	 * Method is called in the beginning to initialize new incoming connections
	 * and give them a port.
	 */

	void listen() {
		LOGGER.info("Listening on port " + port + " -> von Thread: " + Thread.currentThread().getName());

		while (true) {
			LOGGER.finer("Receiving ...");
			try {
				communication.receive(port, true, false);
				//setzt port counter hoch wenn port eingang hat
				portcounter++;

				//sendet message mit neuem port für client
				communication.send(new Message("localhost", port, new Integer(port + portcounter)), port, true);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}

			//für jeden client neuen primeserver mit neuem port siehe oben
			executor.execute(new PrimeServer(port + portcounter));
		}
	}

	/**
	 * Method is used when a connection was set for a new port to run the Prime
	 * Search Threadbased on server.
	 */
	public void run() {
		requestzaehler = 0;

		//nimmt aktuellen aufrufenden thread
		LOGGER.info("Listening on port " + port + " -> von Thread: " + Thread.currentThread().getName());

		while (true) {
			Long request = null;

			if (requestzaehler == anzahlAnfragen) {
				try {

					//cleanup gibt den port wieder frei
					communication.cleanup();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
			requestzaehler++;

			LOGGER.finer("Receiving ...");
			try {
				request = (Long) communication.receive(port, true, false).getContent();
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}

			LOGGER.fine(request.toString() + " received.");

			LOGGER.finer("Sending ...");
			try {

				//berechent primzahl und sendet direkt nachricht an client
				communication.send(new Message("localhost", port, new Boolean(primeService(request.longValue()))), port,
						true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			LOGGER.finer("message sent.");

		}
		LOGGER.info(Thread.currentThread().getName() + " ist nun fertig auf port " + port);
	}

	public static void main(String[] args) {
		//pool selection
		boolean end = false;
		Scanner eingabe = new Scanner(System.in);
		while(!end) {
			System.out.println("Select The Pool You Want:"
					+ "\n1: Cached Thread Pool"
					+ "\n2: Fixed Thread Pool [Length: " + POOLSIZE + "]");
			
			switch(eingabe.nextLine()) {
			case "1": 
				executor = Executors.newCachedThreadPool();
				end = true;
				break;
			case "2":
				executor = Executors.newFixedThreadPool(POOLSIZE);
				end = true;
				break;
			default:
				System.err.println("Inputerror: Please provide a correct value!");
			}
		}
		eingabe.close();
		
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
		
		//aufgabe d
		new ThreadCounter().start();

		// primeserver wird port zugewiesen, mit listen wegen der Schleife
		new PrimeServer(port).listen();
	}
}
