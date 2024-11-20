package classes;

import java.net.*;
import java.util.ArrayList;

public class Partita extends Thread {
    private ArrayList<Giocatore> giocatori;
    private Dealer dealer;
    static int playersNumber = 0;

    Partita(ArrayList<Socket> sockets){
        this.dealer = new Dealer();
        for (Socket socket : sockets) {
            this.giocatori.add(new Giocatore("P"+Integer.toString(++playersNumber), 1000,0, new ArrayList<Carta>(), socket, false));
        }
    }

    
}