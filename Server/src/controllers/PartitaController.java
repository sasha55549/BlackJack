package controllers;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import classes.Carta;
import classes.Dealer;
import classes.Giocatore;
import classes.Mano;
import classes.Message;
import services.ClientCommunicationService;
import services.ClientService;

public class PartitaController extends Thread {
    private ArrayList<Giocatore> giocatori;
    private ArrayList<Carta> mazzo;
    private Dealer dealer;
    private HashMap<String,Integer> punteggi;
    private HashMap<Giocatore,ClientCommunicationService> connections;
    static int playersNumber = 0;

    public PartitaController (ArrayList<Socket> sockets){
        this.mazzo = new ArrayList<Carta>();
        this.dealer = new Dealer("D1",new Mano(),false);
        this.punteggi.put("D1",0);
        for (Socket socket : sockets) {
            Giocatore giocatore = new Giocatore("P"+Integer.toString(++playersNumber), 1000,0, new Mano(), socket, false);
            this.giocatori.add(giocatore);
            this.connections.put(giocatore, new ClientCommunicationService(this, socket, false, giocatore));
            this.punteggi.put("P"+Integer.toString(playersNumber), 0);
        }
    }

    private void connessioni(){
        for (Giocatore giocatore : connections.keySet()) {
            connections.get(giocatore).start();
        }
    }

    private void generaMazzo(){
        for (int i=0;i<6;i++){
            for (String valore : Carta.VALORI) {
                for (String seme : Carta.SEMI) {
                    mazzo.add(new Carta(valore, seme));
                }
            }
        }
    }

    private void mischiaMazzo(){
        for (int i=0;i<3;i++){
            Collections.shuffle(mazzo);
        }
    }

    private boolean allPlayersStayed(){
        boolean allPlayersStayed = true;
        for (Giocatore giocatore : this.giocatori) {
            if (!giocatore.isStayed()) {
                 allPlayersStayed=false;
                 break;
            }
        }
        return allPlayersStayed;
    }

    private void calcolaPunteggi(){
            for (Giocatore giocatore : giocatori) {
                int p=0, assi=0;
                for(int i=0;i<giocatore.getMano().size();i++){
                    String valore = giocatore.getMano().get(i).getValore();
                    switch (valore) {
                        case "K":
                            p+=10;
                            break;
                        case "Q":
                            p+=10;
                            break;
                        case "J":
                            p+=10;
                            break;
                        case "A":
                            p+=11;
                            assi++;
                            break;
                        default:
                            p+=Integer.parseInt(valore);
                            break;
                    }
                    while(p>21 && assi>0) {
                        p-=10;
                        assi--;
                    }
                    punteggi.put(giocatore.getPlayerId(), p);
                }
            }
            int dealerManoSize=dealer.getMano().size();
            if (!allPlayersStayed() && dealerManoSize==2) dealerManoSize-=1; //non fa vedere il punteggio inclusa la carta coperta
            int p=0;
            int assi=0;
            for (int i=0;i<dealerManoSize;i++) {
                String valore = dealer.getMano().get(i).getValore();
                    switch (valore) {
                        case "K":
                            p+=10;
                            break;
                        case "Q":
                            p+=10;
                            break;
                        case "J":
                            p+=10;
                            break;
                        case "A":
                            p+=11;
                            assi++;
                            break;
                        default:
                            p+=Integer.parseInt(valore);
                            break;
                    }
                    while(p>21 && assi>0) {
                        p-=10;
                        assi--;
                    }
                    punteggi.put(dealer.getPlayerId(), p);
            }
            
        }

    //TODO
    public synchronized Message turno(Message message) {
        return null;
    }

    private void distribuisciCarte(){
        for (Giocatore giocatore : giocatori) {
            giocatore.hit(mazzo.removeFirst());
        }
        dealer.hit(mazzo.removeFirst());
        for (Giocatore giocatore : giocatori) {
            giocatore.hit(mazzo.removeFirst());
        }
        dealer.hit(mazzo.removeFirst());
    }
    @Override
    public void run(){
        generaMazzo();
        mischiaMazzo();
        distribuisciCarte();
        calcolaPunteggi();
        while (!allPlayersStayed()) {
            for (Giocatore giocatore : giocatori) {
                if (giocatore.isStayed()) continue;
                //ricevi richiesta se è il turno del giocatore
                //rispondi SI, Azione
                //Attesa ricezione azione del giocatore
                //Switch con varie azioni possibili - controllo la possibilita' di fare una determinata azione
                //Ricalcolo punteggi e verifica vincita o perdita
            }
        }
        while (punteggi.get(dealer.getPlayerId())<17) {
            dealer.hit(mazzo.removeFirst());
            calcolaPunteggi();
        }
    }
    
}