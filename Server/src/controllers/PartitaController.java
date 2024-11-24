package controllers;

import java.net.*;
import java.util.ArrayList;
import java.util.Collections;

import classes.Carta;
import classes.Dealer;
import classes.Giocatore;

public class PartitaController extends Thread {
    private ArrayList<Giocatore> giocatori;
    private ArrayList<Carta> mazzo;
    private Dealer dealer;
    static int playersNumber = 0;

    public PartitaController (ArrayList<Socket> sockets){
        this.dealer = new Dealer("D1",new ArrayList<Carta>(),false);
        for (Socket socket : sockets) {
            this.giocatori.add(new Giocatore("P"+Integer.toString(++playersNumber), 1000,0, new ArrayList<Carta>(), socket, false));
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

    //TODO
    private boolean turno() {
        return false;
    }

    @Override
    public void run(){
        generaMazzo();
        mischiaMazzo();
        while (!allPlayersStayed()) {
            for (Giocatore giocatore : giocatori) {
                if (giocatore.isStayed()) continue;
            }
        }
    }
    
}