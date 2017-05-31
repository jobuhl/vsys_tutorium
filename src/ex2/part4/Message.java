package ex2.part4;

import java.io.Serializable;

public class Message
implements Serializable {
    private String hostname;
    private int port;
    private Object content;

    public Message(String hostname, int port, Object content) {
        this.hostname = hostname;
        this.port = port;
        this.content = content;
    }

    public String getHostname() {
        return this.hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Object getContent() {
        return this.content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}