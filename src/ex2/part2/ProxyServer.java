package ex2.part2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ProxyServer {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {

		System.out.println("Starting Proxy Webserver");

		int port = 0;
		String filename = null;
		Scanner myS = new Scanner(System.in);

		System.out.println("Enter Port: ");
		port = myS.nextInt();
		System.out.println("Enter Log Filename: ");
		filename = myS.next();

		// Neuer Server anlegen
		final ServerSocket server = new ServerSocket(port);
		String content = "";
		System.out.println("Listening on port : " + port);

		while (true) {
			try (Socket socket = server.accept()) {

				InputStreamReader isr = new InputStreamReader(socket.getInputStream());
				BufferedReader reader = new BufferedReader(isr);
				String line = reader.readLine();
				String[] get = line.substring(5).split(" "); // Lese erst ab
																// Zeichen 5
				String getRequest = get[0];
				String regex = "[-a-zA-Z0-9+&@#%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

				boolean bad_request = Pattern.matches(regex, getRequest);
				System.out.println("REGEX: "+bad_request);

				System.out.println(getRequest);
				System.out.println(bad_request);
				if (bad_request) {

					try {

						BufferedReader read = new BufferedReader(new FileReader("/Users/Jojo/Library/Mobile Documents/com~apple~CloudDocs/htwg/sem5/VSYS/vsys_tutorium/src/ex2/part2/"+getRequest + ".html"));
						content = read.readLine();
					} catch (FileNotFoundException ex) {
						BufferedReader read = new BufferedReader(new FileReader("/Users/Jojo/Library/Mobile Documents/com~apple~CloudDocs/htwg/sem5/VSYS/vsys_tutorium/src/ex2/part2/404.html"));
						content = read.readLine();
					}
				} else {
					BufferedReader read = new BufferedReader(new FileReader("/Users/Jojo/Library/Mobile Documents/com~apple~CloudDocs/htwg/sem5/VSYS/vsys_tutorium/src/ex2/part2/badrequest.html"));
					content = read.readLine();
				}
				String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + content;
				socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));

				FileWriter file = new FileWriter(filename, true);
				Date today = new Date();

				while (!line.isEmpty()) {
					System.out.println(line);
					file.append(today.getHours() + ":" + today.getMinutes() + ":" + today.getSeconds() + " Request: "
							+ line);
					file.append(System.getProperty("line.separator"));
					line = reader.readLine();
				}
				file.append(today.getHours() + ":" + today.getMinutes() + ":" + today.getSeconds() + " Response: "
						+ httpResponse);
				file.close();

			}

		}
	}
}