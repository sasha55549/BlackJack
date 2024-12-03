package services;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import classes.Message;

public class ClientService {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    public ClientService() {
    }
    public ClientService(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.socket = socket;
        this.in = in;
        this.out = out;
    }
    public void sendMessage(Message message) {
        if(socket.isClosed()) {
            return;
        }
        try {
            out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public Message recieveMessage() {
        if(socket.isClosed()) {
            return null;
        }
        try {
            Object oggetto = in.readObject();
            if(oggetto instanceof Message) {
                Message message = (Message) oggetto;
                return message;
            }
            else {
                throw new IOException("Message is not an instance of Message");
            }
        } catch (ClassNotFoundException | IOException e) {          
            System.out.println("Error during message passing");              
            return null;
        }
    }
}
