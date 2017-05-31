package ex2.part2;
import java.io.*;
import java.net.*;
 
public class MySocketServer {
	private ServerSocket socket;
	private int port;
	private String logfile;
	
	public MySocketServer(int port, String logfile) throws IOException {
		this.port=port;
		socket=new ServerSocket(port);
		this.logfile = logfile;
	}

	public void listen() {
		while(true) {
			try {
				System.out.println("Server: listening on port "+port);
				Socket incomingConnection = socket.accept();
				MySocketServerConnection connection = new MySocketServerConnection(incomingConnection, logfile);
				connection.start();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
