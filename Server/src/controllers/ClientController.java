package controllers;
import java.net.Socket;
import java.util.ArrayList;

public class ClientController {
    private ArrayList<Socket> clients = new ArrayList<>();

    public ClientController() {
        
    }

    public ClientController(Socket socket) {
        clients.add(socket);
    }
    public boolean startGame() {
        if(clients.size()>2) {
            System.out.println("Game starting");
            new PartitaController(clients).start();
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
