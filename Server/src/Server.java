import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
            Server server = new Server();
            while (true) {
                System.out.println("Listening on port " + PORT);
                Socket socket = serverSocket.accept();
                System.out.println("Connection accepted by " + serverSocket.getInetAddress().toString());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                if(server.getClientController()!=null) {
                    server.getClientController().addClient(socket);
                    server.getClientController().addObjectInputStream(in);
                    server.getClientController().addObjectOutputStream(out);
                }
                else {
                    server.setHandler(new ClientController(socket, in, out));
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
