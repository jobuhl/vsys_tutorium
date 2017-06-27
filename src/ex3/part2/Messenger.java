package ex3.part2;
import ex2.part4.*;

import rm.serverAdmin.ServerAdmin;
import rm.serverAdmin.ServerConfig;
import java.io.IOException;
import java.util.logging.Logger;

public class Messenger implements Runnable {
    private ServerAdmin serverAdmin;
    private Message message;
    private Component communication;
    private Logger logger = Logger.getLogger(Messenger.class.getName());

    public Messenger(Message message, ServerAdmin serverAdmin, Component communication) {
        this.message = message;
        this.serverAdmin =  serverAdmin;
        this.communication = communication;
    }

    @Override
    public void run() {
        ServerConfig serverConfig = serverAdmin.bind();

        try {
            communication.send(message, serverConfig.getReceivePort(), true);
            logger.info("message port: " + message.getPort());
            serverAdmin.release(serverConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
