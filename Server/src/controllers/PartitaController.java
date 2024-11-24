package controllers;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import classes.Carta;
import classes.Dealer;
import classes.Giocatore;
import classes.Mano;

public class PartitaController extends Thread {
    private ArrayList<Giocatore> giocatori;
    private ArrayList<Carta> mazzo;
    private Dealer dealer;
    private HashMap<String,Integer> punteggi;
    static int playersNumber = 0;

    public PartitaController (ArrayList<Socket> sockets){
        this.dealer = new Dealer("D1",new Mano(),false);
        this.punteggi.put("D1",0);
        for (Socket socket : sockets) {
            this.giocatori.add(new Giocatore("P"+Integer.toString(++playersNumber), 1000,0, new Mano(), socket, false));
            this.punteggi.put("P"+Integer.toString(playersNumber), 0);
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
                int p=0;
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
                            if (p+11<=21) p+=11;
                            else p+=1;
                            break;
                        default:
                            p+=Integer.parseInt(valore);
                            break;
                    }
                    punteggi.put(giocatore.getPlayerId(), p);
                }
            }
            int dealerManoSize=dealer.getMano().size();
            if (!allPlayersStayed() && dealerManoSize==2) dealerManoSize-=1; //non fa vedere il punteggio inclusa la carta coperta
            int p=0;
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
                            if (p+11<=21) p+=11;
                            else p+=1;
                            break;
                        default:
                            p+=Integer.parseInt(valore);
                            break;
                    }
                    punteggi.put(dealer.getPlayerId(), p);
            }
            
        }

    //TODO
    private boolean turno(){
        return false;
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
    }
    
}