package classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Stato  implements Serializable {
    //TODO IMPLEMENTA CLASSE STATO DEL GIOCO
    private static final long serialVersionUID = 5598621473L;
    private ArrayList<Giocatore> giocatori;
    private Mano dealerMano; //TODO pensare di nascondere la carta coperta del dealer
    private HashMap<String,Integer> punteggi;

    public Stato(ArrayList<Giocatore> giocatori,Mano dealerMano,HashMap<String,Integer> punteggi){
        this.giocatori=giocatori;
        this.dealerMano=dealerMano;
        this.punteggi=punteggi;
    }
}
