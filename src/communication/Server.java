package communication;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

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
        System.out.println("SERVER STARTED ON PORT: " + port);
        connectionCounter = 0;

        conServer = new SocketIOServer(getConfig());

        /*
            get all incoming connections to the server
            allow only one connection
         */
        conServer.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient socketIOClient) {
                // allow only one client connection
                if (connectionCounter >= 1)
                    socketIOClient.disconnect();

                // count the connections
                connectionCounter++;

                // set the client
                conClient = socketIOClient;
            }
        });
        /*
            handle all disconnects
         */
        conServer.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient socketIOClient) {
                // count connections
                connectionCounter--;
            }
        });

        /*
            receiving messages from the client
         */
        conServer.addEventListener("msgevent", MessageObject.class, new DataListener<MessageObject>() {
            @Override
            public void onData(SocketIOClient socketIOClient, MessageObject messageObject, AckRequest ackRequest) throws Exception {

            }
        });

        conServer.start();
    }

    public void sendDataToClient(String event, MessageObject object) {
        if (conClient != null)
            conClient.sendEvent(event, object);
    }

    public void sendMessageToClient(String message) {
        if (conClient != null)
            conClient.sendEvent("msgevent", new MessageObject(message));
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
