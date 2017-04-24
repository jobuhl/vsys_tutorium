package ex1.part2.b;

import rm.requestResponse.Component;
import rm.requestResponse.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PrimeClient {
	private static final String HOSTNAME="localhost";
	private static final int PORT=1234;
	private static final long INITIAL_VALUE=(long)1e17;
	private static final String REQUESTMODE = "syncronized";
	private static final long COUNT=20;
	private static final String CLIENT_NAME= PrimeClient.class.getName();

	private Component communication;
	String hostname;
	int port;
	String requestmode;
	long initialValue,count;
	
	public PrimeClient(String hostname,int port,long initialValue, String requestMode,long count) {
		this.hostname=hostname;
		this.port=port;
		this.initialValue=initialValue;
		this.requestmode = requestMode;
		this.count=count;
	}
	
	public void run() throws ClassNotFoundException, IOException {
		communication=new Component();
		for (long i=initialValue;i<initialValue+count;i++) processNumber(i);
    }
	    
    public void processNumber(long value) throws IOException, ClassNotFoundException {
		communication.send(new Message(hostname,port,new Long(value)),false);
		Boolean	isPrime = (Boolean) communication.receive(port,true,true).getContent();
	
		System.out.println(value + ": "+(isPrime.booleanValue() ? "prime" : "not prime"));
	}
	
	public static void main(String args[]) throws IOException, ClassNotFoundException {
		String hostname=HOSTNAME;
		int port=PORT;
		long initialValue=INITIAL_VALUE;
		String requestmode = REQUESTMODE;
		long count=COUNT;		
		
		boolean doExit=false;
				
		String input;
		BufferedReader reader=new BufferedReader(new InputStreamReader(System.in )); 
		
		System.out.println("Welcome to "+CLIENT_NAME+"\n");
		
		while(!doExit) {
			System.out.print("Server hostname ["+hostname+"] > ");
			input=reader.readLine();
			if(!input.equals("")) hostname=input;
			
			System.out.print("Server port ["+port+"] > ");
			input=reader.readLine();
			if(!input.equals("")) port=Integer.parseInt(input);
			
			System.out.print("Prime search initial value ["+initialValue+"] > ");
			input=reader.readLine();
			if(!input.equals("")) initialValue=Integer.parseInt(input);

			System.out.print("Prime search count ["+requestmode+"] > ");
			input=reader.readLine();
			if(!input.equals("")) requestmode=input;

			System.out.print("Prime search count ["+count+"] > ");
			input=reader.readLine();
			if(!input.equals("")) count=Integer.parseInt(input);
			
			new PrimeClient(hostname,port,initialValue,requestmode,count).run();
			
			System.out.println("Exit [n]> ");
			input=reader.readLine();
			if(input.equals("y") || input.equals("j")) doExit=true;
		}
	}
}
	
