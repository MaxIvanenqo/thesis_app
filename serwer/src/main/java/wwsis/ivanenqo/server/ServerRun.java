package wwsis.ivanenqo.server;

public class ServerRun {
    private static Server server;
    public static void main(String[] args) {
        System.out.println("server is running...");
        new Server();
    }
    public static void run(){
        System.out.println("server -> start up");
        server = new Server();
    }
    public static void stop(){
        System.out.println("server -> shut down");
        server.stop();
        server = null;
    }
}
