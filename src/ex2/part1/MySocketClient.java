package ex2.part1;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class MySocketClient {
	private Socket socket;
	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;
	public boolean quit = false;
	
	MySocketClient(String hostname, int port) 
					throws IOException {
		socket=new Socket();
		System.out.print("Client: connecting '"+hostname+
			"' on "+port+" ... ");
		socket.connect(new InetSocketAddress(hostname,port));
		System.out.println("done.");
		objectInputStream=
			new ObjectInputStream(socket.getInputStream());
		objectOutputStream=
			new ObjectOutputStream(socket.getOutputStream());
	}


	public String sendAndReceive(String message) throws Exception {


		objectOutputStream.writeObject(message);




		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				Scanner scanner;
				scanner = new Scanner(System.in);

				while (!quit) {
					System.out.println("Mit 'stop' beenden");

					if(scanner.next().equals("stop")) {
						quit = true;

					} else {
						quit = false;
					}

				}
			}
		});


		t2.start();

		while(!quit) {
			Thread.sleep(5000);
			System.out.println("Client: send "+message);
		}


		return "Client: received '"
			+(String)objectInputStream.readObject()+"'";


	}
	
	public void disconnect() 
					throws IOException {
		try {
			socket.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
