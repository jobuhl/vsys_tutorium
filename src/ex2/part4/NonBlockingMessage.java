package ex2.part4;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
//import rm.requestResponse.Message;
import ex2.part4.Message;

public class NonBlockingMessage
extends Thread {
    private Message message = null;
    private ObjectInputStream objectInputStream;
    private ServerSocket serverSocket = null;
    private Socket incomingConnection = null;

    public NonBlockingMessage(ObjectInputStream objectInputStream, ServerSocket serverSocket) {
        this.objectInputStream = objectInputStream;
        this.serverSocket = serverSocket;
    }

    //Client Reader wird nur für nicht blockierende Verbindung benötigt
    @Override
    public void run() {
        ObjectInputStream tmpObjectInputStream = this.objectInputStream;
        if (this.serverSocket != null) {
            try {
                this.incomingConnection = this.serverSocket.accept();
                ObjectOutputStream unusedObjectOutputStream = new ObjectOutputStream(this.incomingConnection.getOutputStream());
                tmpObjectInputStream = new ObjectInputStream(this.incomingConnection.getInputStream());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            this.setMessage((Message)tmpObjectInputStream.readObject());
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public synchronized void setMessage(Message message) {
        this.message = message;
    }

    public synchronized Message getMessage() {
        return this.message;
    }
}