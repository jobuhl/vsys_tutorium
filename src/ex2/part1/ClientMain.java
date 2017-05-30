package ex2.part1;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;

public class ClientMain {
	private static final int port = 1234;
	private static final String hostname = "localhost";
	private static BufferedReader reader;
	private static String clientName;
	private static boolean input = true;
	private static String prozess;

	public static void inputSet() {
		input = false;
	}

	public static void main(String args[]) {
		try {

			System.out.print("Client: Enter name> ");
			reader = new BufferedReader(new InputStreamReader(System.in));
			clientName = reader.readLine();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		prozess = ManagementFactory.getRuntimeMXBean().getName();

		// INPUT THREAD
		int counter = 0;
		Thread in = new Thread(new UserInput());
		in.start();

		// MESSAGE THREADS
		while (input) {
			try {
				Thread t = new Thread(new MySocketClient(hostname, port,prozess +" - "+ clientName + " - " + counter));
				t.start();
				t.sleep((int)(Math.random() * 10000));
			} catch (IOException | InterruptedException e) {		
				e.printStackTrace();
			}
			counter++;
		}

	}
}
