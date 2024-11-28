package controllers;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientController {
    private ArrayList<Socket> clients = new ArrayList<>();
    private ArrayList<ObjectInputStream> inList = new ArrayList<>();
    private ArrayList<ObjectOutputStream> outList = new ArrayList<>();

    public ClientController(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        clients.add(socket);
        inList.add(in);
        outList.add(out);
    }
    public boolean startGame() {
        if(clients.size()>=2) {
            System.out.println("Game starting");
            System.out.println(clients.size());
            new PartitaController(clients, inList, outList).start();
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
    public ArrayList<ObjectInputStream> getInList() {
        return inList;
    }
    public ArrayList<ObjectOutputStream> getOutList() {
        return outList;
    }
    
}
