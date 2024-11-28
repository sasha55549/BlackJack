package classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.Serializable;

public class Stato implements Serializable{
    //TODO IMPLEMENTA CLASSE STATO DEL GIOCO
    private ArrayList<Giocatore> giocatori;
    private Mano dealerMano; //TODO pensare di nascondere la carta coperta del dealer
    private HashMap<String,Integer> punteggi;

    public Stato(ArrayList<Giocatore> giocatori,Mano dealerMano,HashMap<String,Integer> punteggi){
        this.giocatori=giocatori;
        this.dealerMano=dealerMano;
        this.punteggi=punteggi;
    }

    public ArrayList<Giocatore> getGiocatori() {
        return this.giocatori;
    }

    public void setGiocatori(ArrayList<Giocatore> giocatori) {
        this.giocatori = giocatori;
    }

    public Mano getDealerMano() {
        return this.dealerMano;
    }

    public void setDealerMano(Mano dealerMano) {
        this.dealerMano = dealerMano;
    }

    public HashMap<String,Integer> getPunteggi() {
        return this.punteggi;
    }

    public void setPunteggi(HashMap<String,Integer> punteggi) {
        this.punteggi = punteggi;
    }

}
