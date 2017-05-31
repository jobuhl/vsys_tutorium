package ex2.part3;

import ex2.part4.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PrimeClient extends Thread {
	private static final String HOSTNAME = "localhost";
	private static final int PORT = 1234;
	private static final long INITIAL_VALUE = (long) 1e17;
	private static final long COUNT = 20;
	private static final String CLIENT_NAME = PrimeClient.class.getName();

	// Time Variables
	long processingTimeStart;
	long waitingTimeStart;
	long communicationTimeStart;
	long processingTimeEnd;
	long waitingTimeEnd;
	long communicationTimeEnd;

	long pTotal = 0;
	long wTotal = 0;
	long cTotal = 0;

	long pCount = 0;
	long wCount = 0;
	long cCount = 0;

	private Component communication;
	String hostname;
	int port;
	int commport;
	long initialValue, count;
	boolean requestMode;
	boolean blocking;

	public PrimeClient(String hostname, int port, long initialValue, long count, boolean blocking) {
		this.hostname = hostname;
		this.port = port;
		this.initialValue = initialValue;
		this.count = count;
		this.blocking = blocking;
	}

	public void run() {
		communication = new Component();

		// Client sendet auf Port 1234 um dann einen Kommunikationsport zu
		// erhalten
		try {
			communication.send(new Message(hostname, port, new Boolean(true)), port, true);
			commport = (Integer) communication.receive(port, true, true).getContent();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		for (long i = initialValue; i < initialValue + count; i++)
			try {
				processNumber(i);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public void processNumber(long value) throws IOException, ClassNotFoundException {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		// Client kann nun die Startzahl zur Primzahlberechnung senden auf dem
		// neuen Commport
		communication.send(new Message(hostname, commport, new Long(value)), commport, true);
		// kurz warten um den "nicht-Prime" numbers Zeit zu geben

		if (!blocking) {
			Message message;
			Boolean isPrime;
			int zaehler = 0;
			System.out.print(value + ": ");

			processingTimeStart = System.currentTimeMillis();
			while (true) {
				// Resultat erhalten
				message = communication.receive(commport, false, true);
				// wenn ein Resultat gekommen ist dann springen wir raus
				if (message != null) {
					isPrime = (Boolean) message.getContent();
					break;
				}
				// wartepunkt setzen ausser beim ersten mal
				zaehler++;
				if (zaehler > 1) {
					System.out.print(".");
				}
				// 1 Sekunde warten
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			processingTimeEnd = System.currentTimeMillis();
			pTotal += (processingTimeEnd - processingTimeStart);
			pCount++;

			System.out.print((isPrime.booleanValue() ? "prime" : "not prime"));

			System.out.println(" | p: " + (processingTimeEnd - processingTimeStart) + "(" + (pTotal / pCount) + ") ms");
		} else {
			processingTimeStart = System.currentTimeMillis();
			Boolean isPrime = (Boolean) communication.receive(commport, true, true).getContent();
			processingTimeEnd = System.currentTimeMillis();
			pTotal += (processingTimeEnd - processingTimeStart);
			pCount++;

			System.out.print(value + ": " + (isPrime.booleanValue() ? "prime" : "not prime"));

			System.out.println(" | p: " + (processingTimeEnd - processingTimeStart) + "(" + (pTotal / pCount) + ") ms");

		}

		// Ausgabe für Zeitberechnung
	}

	public static void main(String args[]) throws IOException, ClassNotFoundException {
		String hostname = HOSTNAME;
		int port = PORT;
		long initialValue = INITIAL_VALUE;
		long count = COUNT;
		boolean blocking = false;
		boolean blockingAndNebenlaeufig = false;

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

			System.out.print("blocking [" + blocking + "] > ");
			input = reader.readLine();
			if (!input.equals(""))
				blocking = Boolean.parseBoolean(input);

			System.out.print("blocking und nebenlaeufig [" + blockingAndNebenlaeufig + "] > ");
			input = reader.readLine();
			if (!input.equals(""))
				blockingAndNebenlaeufig = Boolean.parseBoolean(input);

			System.out.print("Prime search initial value [" + initialValue + "] > ");
			input = reader.readLine();
			if (!input.equals(""))
				initialValue = Integer.parseInt(input);

			System.out.print("Prime search count [" + count + "] > ");
			input = reader.readLine();
			if (!input.equals(""))
				count = Integer.parseInt(input);

			if (blockingAndNebenlaeufig) {
				PrimeClient pc = new PrimeClient(hostname, port, initialValue, count, true);
				pc.start();
				while (pc.isAlive()) {
					System.out.print(".");
					try {
						pc.sleep(350);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			} else {
				new PrimeClient(hostname, port, initialValue, count, blocking).run();
			}

			// System.out.println("Exit [j/n]> ");
			// input = reader.readLine();
			// if (input.equals("y") || input.equals("j"))
			doExit = true;
		}
	}
}
