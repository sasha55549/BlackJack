import java.net.ServerSocket;
import java.net.Socket;

import controllers.ClientController;


public class Server {
    public static final int PORT = 50000;
    private ClientController clientController;
    public static void main(String[] args) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(PORT);
            while (true) {
                System.out.println("Listening on port " + PORT);
                Socket socket = serverSocket.accept();
                System.out.println("Connection accepted by " + serverSocket.getInetAddress().toString());

                Server server = new Server();
                if(server.getClientController()!=null) {
                    server.getClientController().addClient(socket);
                }
                else {
                    server.setHandler(new ClientController());
                }
                if(!server.getClientController().startGame()) {
                    System.out.println("Waiting for players to join the game");
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    public ClientController getClientController() {
        return clientController;
    }
    public void setHandler(ClientController clientController) {
        this.clientController = clientController;
    }
}
