package classes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientService {
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    ClientService(Socket clientSocket){
        try {
            this.clientSocket = clientSocket;
            this.in = new ObjectInputStream(clientSocket.getInputStream());
            this.out = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Errore di comunicazione");
            e.printStackTrace();
        }
    }
}