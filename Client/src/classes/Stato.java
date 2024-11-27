package classes;

import java.util.ArrayList;
import java.util.HashMap;

public class Stato {
    //TODO IMPLEMENTA CLASSE STATO DEL GIOCO
    private ArrayList<Giocatore> giocatori;
    private Mano dealerMano; //TODO pensare di nascondere la carta coperta del dealer
    private HashMap<String,Integer> punteggi;

    public Stato(ArrayList<Giocatore> giocatori,Mano dealerMano,HashMap<String,Integer> punteggi){
        this.giocatori=giocatori;
        this.dealerMano=dealerMano;
        this.punteggi=punteggi;
    }
}
