package ex2.part1;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientMain {
	private static final int port = 1234;
	private static final String hostname = "localhost";
	private static BufferedReader reader;
	private static String clientName;
	private static boolean breaking = true;

	private static void setFalse() {
		breaking = false;
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

		//Interne Klasse für jeden Thread der die Abbruchbedingung abhandelt
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
						ClientMain.setFalse();
						System.out.println("---Request will be stoped---");
						Thread.currentThread().stop();
					}
				}
			}
		});
		thread.start();

		//solange stop nicht eingegeben wird -> anfrage an server
		while (breaking) {
			try {
				Thread thread1 = new Thread(new MySocketClient(hostname, port, clientName + "-" + counter));
				thread1.start();
				thread1.sleep((int)(Math.random() * 10000));
			} catch (IOException | InterruptedException e) {		
				e.printStackTrace();
			}
			counter++;
		}

	}
}
