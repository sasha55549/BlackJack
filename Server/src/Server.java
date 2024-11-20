import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    public static final int PORT = 50000;
    private ClientHandler handler;
    public static void main(String[] args) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(PORT);
            while (true) {
                System.out.println("Listening on port " + PORT);
                Socket socket = serverSocket.accept();
                System.out.println("Connection accepted by " + serverSocket.getInetAddress().toString());

                Server server = new Server();
                if(server.getHandler()!=null) {
                    server.getHandler().addClient(socket);
                }
                else {
                    server.setHandler(new ClientHandler());
                }
                if(!server.getHandler().startGame()) {
                    System.out.println("Waiting for players to join the game");
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    public ClientHandler getHandler() {
        return handler;
    }
    public void setHandler(ClientHandler handler) {
        this.handler = handler;
    }
}
