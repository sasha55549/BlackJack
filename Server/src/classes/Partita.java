package classes;

import java.net.*;
import java.util.ArrayList;
import java.util.Collections;

public class Partita extends Thread {
    private ArrayList<Giocatore> giocatori;
    private ArrayList<Carta> mazzo;
    private Dealer dealer;
    static int playersNumber = 0;

    Partita(ArrayList<Socket> sockets){
        this.dealer = new Dealer("D1",new ArrayList<Carta>(),false);
        for (Socket socket : sockets) {
            this.giocatori.add(new Giocatore("P"+Integer.toString(++playersNumber), 1000,0, new ArrayList<Carta>(), socket, false));
        }
    }

    public void generaMazzo(){
        for (int i=0;i<6;i++){
            for (String valore : Carta.VALORI) {
                for (String seme : Carta.SEMI) {
                    mazzo.add(new Carta(valore, seme));
                }
            }
        }
    }

    public void mischiaMazzo(){
        for (int i=0;i<3;i++){
            Collections.shuffle(mazzo);
        }
    }

    public boolean allPlayersStayed(){
        boolean allPlayersStayed = true;
        for (Giocatore giocatore : this.giocatori) {
            if (!giocatore.isStayed()) {
                 allPlayersStayed=false;
                 break;
            }
        }
        return allPlayersStayed;
    }

    @Override
    public void run(){

        allPlayersStayed();
    }
    
}