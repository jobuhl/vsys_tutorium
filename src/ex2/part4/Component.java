package ex2.part4;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Handler;
import java.util.logging.Level;

import ex2.part4.NonBlockingMessage;
import ex2.part4.Message;

public class Component {

    private ServerSocket serverSocket = null;    //Listening Socket
    private Socket socket = null;    //Client Socket
    private Socket incomingConnection = null;    //Entsteht beim accept des Server Socket
    private NonBlockingMessage nonBlockingMessage = null;    //Wird benötigt für die nicht-blockierenden Aufrufe eines Client/Server.

    //I/O-Streams von 
    private ObjectInputStream objectInputStream = null;
    private ObjectOutputStream objectOutputStream = null;

    //I/O-Streams von incomingConnection
    private ObjectInputStream serverSocketObjectInputStream = null;
    private ObjectOutputStream serverSocketObjectOutputStream = null;

    //Ports/Hostnames der Server/Clients oder zugehörigen Server/Clients
    private int serverPort = 0;
    private int clientPort = 0;
    private String serverHostname = "";
    private String clientHostname = "";

    //Flags ob Server oder Client
    boolean server = false;
    boolean client = false;

    //Lock Monitor auf send oder receive synchronized Blöcke
    Integer sendLock = new Integer(0);
    Integer receiveLock = new Integer(0);


    public void send(Message message, int sendport, boolean complete) throws IOException {
        Integer n = this.sendLock;
        synchronized (n) {
            //Prüft ob serverSocket null ist, wenn ja ist es keine Server Connection
            //Prüft ob this.client bereits true, dann gehört die Connection schon zu einem Client

            //Client Fall
            if (this.serverSocket == null || this.client) {
                this.client = true;
                if (this.socket == null) {
                    this.createSocket(sendport, message.getHostname(), false);
                } else if (this.serverPort != sendport || !this.serverHostname.equals(message.getHostname())) {
                    this.socket.close();
                    this.createSocket(sendport, message.getHostname(), false);
                }
                this.objectOutputStream.writeObject(message);
                this.objectOutputStream.flush();

                //Server Fall
            } else {
                if (this.serverPort != sendport) {
                    if (this.socket == null) {
                        this.createSocket(sendport, message.getHostname(), true);
                    } else if (this.clientPort != sendport || !this.clientHostname.equals(message.getHostname())) {
                        this.socket.close();
                        this.createSocket(sendport, message.getHostname(), true);
                    }
                    this.objectOutputStream.writeObject(message);
                    this.objectOutputStream.flush();
                    if (complete) {
                        this.reset();
                    }
                    //"Normalfall" port == sendport, schickt Nachricht direkt an Client weiter
                } else {
                    this.serverSocketObjectOutputStream.writeObject(message);
                    this.serverSocketObjectOutputStream.flush();
                    if (complete) {
                        this.reset();
                    }
                }
            }
        }
    }

    public Message receive(int port, boolean blocking, boolean complete) throws IOException, ClassNotFoundException {
        Integer n = this.receiveLock;
        synchronized (n) {
            Message message = null;
            //Server Fall
            if ((this.socket == null || this.server) && !this.client) {
                this.server = true;
                if (this.serverSocket == null) {
                    this.serverPort = port;
                    this.serverSocket = new ServerSocket(port);
                    this.incomingConnection = this.serverSocket.accept();
                    this.serverSocketObjectOutputStream = new ObjectOutputStream(this.incomingConnection.getOutputStream());
                    this.serverSocketObjectInputStream = new ObjectInputStream(this.incomingConnection.getInputStream());
                } else {
                    if (this.serverPort != port) {
                        this.serverSocket.close();
                        this.serverPort = port;
                        this.serverSocket = new ServerSocket(port);
                    }
                    this.incomingConnection = this.serverSocket.accept();
                    this.serverSocketObjectOutputStream = new ObjectOutputStream(this.incomingConnection.getOutputStream());
                    this.serverSocketObjectInputStream = new ObjectInputStream(this.incomingConnection.getInputStream());
                }
                if (blocking) {
                    message = (Message) this.serverSocketObjectInputStream.readObject();
                } else {
                    if (this.nonBlockingMessage == null) {
                        this.nonBlockingMessage = new NonBlockingMessage(this.serverSocketObjectInputStream, null);
                        this.nonBlockingMessage.start();
                    }
                    NonBlockingMessage clientReader = this.nonBlockingMessage;
                    synchronized (clientReader) {
                        message = this.nonBlockingMessage.getMessage();
                        if (message != null) {
                            this.nonBlockingMessage = null;
                        }
                    }
                }

                //Client Fall
            } else {
                if (this.serverPort != port) {
                    if (this.serverSocket == null) {
                        this.clientPort = port;
                        this.serverSocket = new ServerSocket(port);
                    } else {
                        if (this.clientPort != port) {
                            this.serverSocket.close();
                            this.clientPort = port;
                            this.serverSocket = new ServerSocket(port);
                        }
                    }
                    if (blocking) {
                        this.incomingConnection = this.serverSocket.accept();
                        this.serverSocketObjectOutputStream = new ObjectOutputStream(this.incomingConnection.getOutputStream());
                        this.serverSocketObjectInputStream = new ObjectInputStream(this.incomingConnection.getInputStream());
                    }
                    if (blocking) {
                        message = (Message) this.serverSocketObjectInputStream.readObject();
                        if (complete) {
                            this.reset();
                        }
                    } else {
                        if (this.nonBlockingMessage == null) {
                            this.nonBlockingMessage = new NonBlockingMessage(null, this.serverSocket);
                            this.nonBlockingMessage.start();
                        }
                        NonBlockingMessage clientReader = this.nonBlockingMessage;
                        synchronized (clientReader) {
                            message = this.nonBlockingMessage.getMessage();
                            if (message != null) {
                                this.nonBlockingMessage = null;
                                if (complete) {
                                    this.reset();
                                }
                            }
                        }
                    }
                } else if (blocking) {
                    message = (Message) this.objectInputStream.readObject();
                    if (complete) {
                        this.reset();
                    }
                } else {
                    if (this.nonBlockingMessage == null) {
                        this.nonBlockingMessage = new NonBlockingMessage(this.objectInputStream, null);
                        this.nonBlockingMessage.start();
                    }
                    NonBlockingMessage clientReader = this.nonBlockingMessage;
                    synchronized (clientReader) {
                        message = this.nonBlockingMessage.getMessage();
                        if (message != null) {
                            this.nonBlockingMessage = null;
                            if (complete) {
                                this.reset();
                            }
                        }
                    }
                }
            }
            return message;
        }
    }

    public void cleanup() throws IOException {
        this.reset();
        if (this.serverSocket != null) {
            this.serverSocket.close();
        }
        if (this.incomingConnection != null) {
            this.incomingConnection.close();
        }
        this.server = false;
        this.serverPort = 0;
        this.client = false;
    }

    private void createSocket(int port, String hostname, boolean client) throws IOException {
        if (client) {
            this.clientPort = port;
            this.clientHostname = hostname;
        } else {
            this.serverPort = port;
            this.serverHostname = hostname;
        }
        this.socket = new Socket();
        this.socket.connect(new InetSocketAddress(hostname, port));
        this.objectInputStream = new ObjectInputStream(this.socket.getInputStream());
        this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
    }

    private void reset() throws IOException {
        if (this.socket != null) {
            this.socket.close();
            this.socket = null;
        }
        if (this.serverSocket == null) {
            this.clientPort = 0;
        }
    }
}