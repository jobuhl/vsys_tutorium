package ex2.part1;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class MySocketServerConnection extends Thread {
	private Socket socket;
	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;

	public MySocketServerConnection(Socket socket) 
					throws IOException {
		this.socket=socket;
		objectOutputStream=
			new ObjectOutputStream(socket.getOutputStream());
		objectInputStream=
			new ObjectInputStream(socket.getInputStream());
		System.out.println("Server: incoming connection accepted.");
	}
	
	public void run() {
		System.out.println("Server: waiting for message ...");




		try {

			if (objectOutputStream != null) {
				String string=(String)objectInputStream.readObject();
				System.out.println("Server: received '"+string+"'");
				objectOutputStream.writeObject("server received "+string);


			} else {
				System.out.println("TEST ELSE");
				socket.close();

			}



		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
