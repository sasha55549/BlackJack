import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler {
    private ArrayList<Socket> clients = new ArrayList<>();

    public ClientHandler() {
        
    }

    public ClientHandler(Socket socket) {
        clients.add(socket);
    }
    public boolean startGame() {
        if(clients.size()>2) {
            System.out.println("Game starting");
            new Partita(clients);
            return true;
        }
        return false;
    }
    public void addClient(Socket socket) {
        clients.add(socket);
    }
    public ArrayList<Socket> getClients() {
        return clients;
    }
}
