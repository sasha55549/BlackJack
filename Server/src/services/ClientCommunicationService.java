package services;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import classes.Giocatore;
import classes.Message;

public class ClientCommunicationService implements Runnable{
    private ClientService clientService;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean turno;
    private Giocatore giocatore;

    public ClientCommunicationService() {

    }
    public ClientCommunicationService(ClientService clientService , Socket socket, boolean turno, Giocatore giocatore) {
        this.clientService = clientService;
        this.giocatore = giocatore;
        this.socket = socket;
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            this.turno = turno;
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    @Override
    public void run() {
        Object input = null;
        while (true) {
            try {
                input = in.readObject();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
            if(input instanceof Message) {
                input = (Message) input;
                if(((Message)input).getMethod().equals("TURNO")) {
                    if(turno) {
                        try {
                            out.writeObject(new Message(200, giocatore.getPlayerId()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if(((Message)input).getMethod().equals("INIZIO")) {
                    //TODO capire chi deve instanziare l'oggetto e come l'oggetto comunicherà con gli altri
                }
            }
        }
    }
    public boolean getTurno() {
        return this.turno;
    }

    public void setTurno(boolean turno) {
        this.turno = turno;
    }
    
}