package ex2.part1;
import java.io.*;
import java.net.*;
 
public class MySocketServer {
	private ServerSocket socket;
	private int port;
	
	public MySocketServer(int port) 
					throws IOException {
		this.port=port;
		socket=new ServerSocket(port);
	}

	public void listen() {
		while(true) {
			try {
				System.out.println("Server: listening on port "+port);
				Socket incomingConnection=socket.accept();
				MySocketServerConnection connection=
					new MySocketServerConnection(incomingConnection);
				connection.start();

				System.out.println("Socket-Adresse: "
						+ incomingConnection.getInetAddress() + " Port:"
						+ incomingConnection.getPort());
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
