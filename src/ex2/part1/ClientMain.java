package ex2.part1;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientMain {
	private static final int port = 1234;
	private static final String hostname = "localhost";
	private static BufferedReader reader;
	private static String clientName;
	private static boolean input = true;

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

		// jeder Input startet einen Thread nummer wird pro aufruf itteriert
		int counter = 0;

		Thread thread = new Thread(new Runnable(){
			private BufferedReader reader;

			@Override
			public void run() {
				System.out.println("Client: To stop Request-> enter (stop)");
				while (true) {
					reader = new BufferedReader(new InputStreamReader(System.in));
					String read = "";

					try {
						read = reader.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}

					if (read.equals("stop")) {
						ClientMain.inputSet();
						System.out.println("---Wurde abgebrochen---");
						break;
					}
				}
			}
		});
		thread.start();

		// MESSAGE THREADS
		while (input) {
			try {
				Thread t = new Thread(new MySocketClient(hostname, port, clientName + "-" + counter));
				t.start();
				t.sleep((int)(Math.random() * 10000));
			} catch (IOException | InterruptedException e) {		
				e.printStackTrace();
			}
			counter++;
		}

	}
}
