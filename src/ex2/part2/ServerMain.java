package ex2.part2;
import java.io.*;
import java.util.Scanner;
 
public class ServerMain {
	private static final int port = 1234;
	private static MySocketServer server;
	
	public static void main(String args[]) {
		try {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Bitte geben Sie den Namen der Logfile an: ");
			String logfile = scanner.nextLine();
			scanner.close();
			server = new MySocketServer(port, logfile);
			server.listen();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
