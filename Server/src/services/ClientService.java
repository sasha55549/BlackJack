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
    public ClientService(Socket socket) {
        this.socket = socket;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
    public void sendMessage(int statusCode, String playerId, Object object) {
        if(socket.isClosed()) {
            return;
        }
        Message message;
        if(object instanceof String) {
            message = new Message(statusCode, playerId, String.valueOf(object));
        }
        else {
            message = new Message(statusCode, playerId, object);
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
