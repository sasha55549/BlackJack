package services;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import classes.Dealer;
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
    private boolean iniziata;
    private Giocatore giocatore;
    private Dealer dealer;
    private Message messaggioImportante;

    public ClientCommunicationService() {

    }
    public ClientCommunicationService(PartitaController partita, Socket socket, boolean turno, boolean iniziata, Giocatore giocatore, Dealer dealer) {
        this.partita = partita;
        this.clientService = new ClientService(socket);
        this.giocatore = giocatore;
        this.dealer = dealer;
        this.socket = socket;
        this.turno = turno;
        this.iniziata = iniziata;
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
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
                                if(turno) {
                                    clientService.sendMessage(new Message(200, giocatore.getPlayerId()));   
                                }
                                else {
                                    clientService.sendMessage(new Message(300, giocatore.getPlayerId()));
                                }
                        } else if(input.getMethod().equals("INIZIO")) {
                            if(iniziata) {
                                clientService.sendMessage(new Message(200, giocatore.getPlayerId(), giocatore));
                                clientService.sendMessage(new Message(200, giocatore.getPlayerId(), dealer.getMano()));
                            }
                        } else {
                            partita.turno(input);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean getTurno() {
        return this.turno;
    }

    public void setTurno(boolean turno) {
        this.turno = turno;
    }
    
    
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
}