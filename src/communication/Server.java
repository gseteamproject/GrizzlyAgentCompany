package communication;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

public class Server implements Runnable {
    private final String server = "localhost";
    private final int port = 9092;

    @Override
    /*
    just an example of the communication, not finished yet.
     */
    public void run() {
        System.out.println("SERVER STARTED ON PORT: " + port);

        final SocketIOServer server = new SocketIOServer(getConfig());
        server.addEventListener("chatevent", ChatObject.class, new DataListener<ChatObject>() {
            @Override
            public void onData(SocketIOClient socketIOClient, ChatObject data, AckRequest ackRequest) throws Exception {
                System.out.println("onData receive");
                server.getBroadcastOperations().sendEvent("chatevent", data);
            }
        });

        server.start();
    }

    public void sendDataToClient() {

    }

    private Configuration getConfig() {
        Configuration config = new Configuration();
        config.setHostname(server);
        config.setPort(port);
        return config;
    }

    private void stopServer() {

    }
}
