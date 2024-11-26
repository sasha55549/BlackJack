package services;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import classes.Giocatore;
import classes.Message;
import controllers.PartitaController;

public class ClientCommunicationService extends Thread{
    private PartitaController partita;
    private ClientService clientService;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean turno;
    private Giocatore giocatore;

    public ClientCommunicationService() {

    }
    public ClientCommunicationService(PartitaController partita, Socket socket, boolean turno, Giocatore giocatore) {
        this.partita = partita;
        this.clientService = new ClientService(socket);
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
        while (true) {
                Message request = clientService.recieveMessage();
                Message response = partita.turno(request);
                clientService.sendMessage(response);


                /*Spostare la logica nel metodo turno
                if(input.getMethod().equals("TURNO")) {
                    if(turno) {
                        try {
                            out.writeObject(new Message(200, giocatore.getPlayerId()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if(((Message)input).getMethod().equals("INIZIO")) {
                    //TODO capire chi deve instanziare l'oggetto e come l'oggetto comunicherà con gli altri
                } */
        }
    }

    //Spostiamo la gestione del turno nella partita quindi eliminiamo
    public boolean getTurno() {
        return this.turno;
    }

    public void setTurno(boolean turno) {
        this.turno = turno;
    }
    
}