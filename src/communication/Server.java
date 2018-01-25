package communication;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;

import jade.lang.acl.ACLMessage;

/*
    SocketIO implementation to communicate with the client website
 */
public class Server implements Runnable {
    private final String server = "localhost";
    private final int port = 9092;

    private SocketIOServer conServer;
    private SocketIOClient conClient;
    private int connectionCounter;

    @Override
    /*
    just an example of the communication, not finished yet.
     */
    public void run() {
        System.out.println("[Server] -> Started on port " + port);
        connectionCounter = 0;

        conServer = new SocketIOServer(getConfig());

        /*
            get all incoming connections to the server
            allow only one connection
         */
        conServer.addConnectListener(socketIOClient -> {
            // allow only one client connection
            if (connectionCounter >= 1) {
                socketIOClient.disconnect();
                return;
            }

            // count the connections
            connectionCounter++;

            // set the client
            conClient = socketIOClient;
        });
        /*
            handle all disconnects
         */
        conServer.addDisconnectListener(socketIOClient -> {
            // count connections
            if (connectionCounter >= 1)
                connectionCounter--;
        });

        /*
            receiving messages from the client
         */
        conServer.addEventListener("msgevent", MessageObject.class, (socketIOClient, messageObject, ackRequest) -> {
            if (messageObject.getMessage() == "stop_server") {
                System.out.println("[Server] -> Client stopped the server!");
                stopServer();
            }
        });

        conServer.start();
    }

    public void sendMessageToClient(MessageObject msgObj) {
        MessageWrapper wrapper = new MessageWrapper(msgObj);

        if (conClient != null)
            conClient.sendEvent("alcevent", wrapper);
    }

    public void sendMessageToClient(ACLMessage acl, String ordertext) {
        MessageWrapper wrapper = new MessageWrapper(new MessageObject(acl, ordertext));

        if (conClient != null)
            conClient.sendEvent("alcevent", wrapper);
    }
    public void sendMessageToClient(String one, String two) {

    }

    public void sendJson(ACLMessage acl, String ordertext, String from, String to) {
        MessageWrapper wrapper = new MessageWrapper(new MessageObject(acl, ordertext));

        if (conClient != null) {
            wrapper.setMessage("{ \"from\": \"" + from + "\", \"to\": \"" + to + "\", \"color\": \"red\", \"text\": \"" + wrapper.getMessage() + "\" }");
            conClient.sendEvent("jsonevent", wrapper);
        }
    }

    /*
    setup configuration
     */
    private Configuration getConfig() {
        Configuration config = new Configuration();
        config.setHostname(server);
        config.setPort(port);
        return config;
    }

    private void stopServer() {
        if (conClient != null)
            conClient.disconnect();

        if (conServer != null)
            conServer.stop();
    }
}
