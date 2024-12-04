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
    private ObjectInputStream in;
    private ObjectOutputStream out;
    //private boolean turno;
    private boolean iniziata;
    private Giocatore giocatore;
    private Message messaggioImportante;

    public ClientCommunicationService() {

    }
    public ClientCommunicationService(PartitaController partita, Socket socket, boolean iniziata, Giocatore giocatore, ObjectInputStream in, ObjectOutputStream out) {
        this.partita = partita;
        this.clientService = new ClientService(socket, in, out);
        this.giocatore = giocatore;
        //this.turno = turno;
        this.iniziata = iniziata;
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if(in.available() == 0) {
                        Object ciao = null;
                            ciao = in.readObject();
                        
                        if(ciao instanceof Message) {
                            Message input = (Message) ciao;
                            if(input.getMethod().equals("TURNO")) {
                                if(partita.getCurrentPlayer().equalsIgnoreCase(giocatore.getPlayerId())) {
                                    clientService.sendMessage(new Message(200, giocatore.getPlayerId(), giocatore));   
                                }
                                else {
                                    clientService.sendMessage(new Message(300, giocatore.getPlayerId(), giocatore));
                                }
                        } else if(input.getMethod().equals("INIZIO")) {
                            if(iniziata) {
                                clientService.sendMessage(new Message(200, giocatore.getPlayerId()));
                                // clientService.sendMessage(new Message(200, giocatore.getPlayerId(), dealer.getMano()));
                            }
                        } 
                         else {
                            clientService.sendMessage(partita.turno(input));
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    public boolean getTurno() {
        return this.turno;
    }

    public void setTurno(boolean turno) {
        this.turno = turno;
    }
    */
    
    
    public boolean getIniziata() {
        return this.iniziata;
    }

    public void setIniziata(boolean iniziata) {
        this.iniziata = iniziata;
    }
    public void setMessaggio(Message messaggioImportante) {
        this.messaggioImportante = messaggioImportante;
    }
    public Message getMessaggio() {
        return messaggioImportante;
    }
    public ObjectInputStream getIn() {
        return in;
    }
    public ObjectOutputStream getOut() {
        return out;
    }
}