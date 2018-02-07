package communication;

public class Communication {
    public static Server server;
    public static long delaytime=5;

    public Communication() {
        server = new Server();

        Thread thread = new Thread(server);
        thread.start();
    }
    public void GUIdelay () throws InterruptedException {
        try {
            Thread.sleep(delaytime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
