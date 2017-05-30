package ex2.part2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Webserver {

	public static void main(String[] args) throws IOException {
		
		System.out.println("Starting Webserver");

		int port = 1234;
		String filename = "";
		Scanner scanner = new Scanner(System.in);

		System.out.println("Enter Port: ");
		port = scanner.nextInt();
		System.out.println("Enter Log Filename: ");
		filename = scanner.next();

		ServerSocket serverSocket = new ServerSocket(port);
		System.err.println("Server started on Port : " + port);


		while (true) {

			System.out.println("Server: listening on port " + port);
			Socket clientSocket = serverSocket.accept();
			System.err.println("New connection");

			BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

			String s;
			Writer fw = null;

			fw = new FileWriter(filename, true);

			while ((s = input.readLine()) != null) {
				try {
					System.out.println(s);
					fw.write(s);
					fw.write("\\n");
					fw.write(System.getProperty("line.separator")); //
					// e.g. "\n"
				} catch (IOException e) {
					System.err.println("Konnte Datei nicht erstellen");
				}

				if (s.isEmpty()) {
					break;
				}
			}
			output.write("HTTP/1.0 200 OK\r\n\r\n<html><body><h1>Hallo Web-Welt</h1></body></html>");

			System.err.println("Connection lost");
			fw.close();
			output.close();
			input.close();
		}
		
	}

}
