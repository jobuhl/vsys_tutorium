package ex2.part1;
import java.io.*;
import java.net.*;

public class MySocketClient implements Runnable {
	private Socket socket;
	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;
	String message;

	MySocketClient(String hostname, int port, String message) throws IOException {
		this.message = message;
		socket = new Socket();
		System.out.print("Client: connecting '" + hostname + "' on " + port + " ... ");
		socket.connect(new InetSocketAddress(hostname, port));
		System.out.println("done.");

		//gibt aus was der Server zurück sendet
		objectInputStream = new ObjectInputStream(socket.getInputStream());
		//gibt aus was zum Server  gesendet wird
		objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
	}

	//Methode zum empfangen und senden
	public String sendAndReceive(String message) throws Exception {
		objectOutputStream.writeObject(message); // SENDE Message
		System.out.println("Client: send " + message);
		return "Client: received '" + (String) objectInputStream.readObject() + "'";
	}

	public void disconnect() throws IOException {
		try {
			socket.close(); //Socket disconnecten
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() { // METHODE für den Thread
		try {
			sendAndReceive(message); //Sende Message
			disconnect();	//Disconnect
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
