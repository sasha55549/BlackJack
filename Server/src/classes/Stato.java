package classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.Serializable;


public class Stato implements Serializable {

    //TODO IMPLEMENTA CLASSE STATO DEL GIOCO
    private static final long serialVersionUID = 5598621473L;
    private ArrayList<Giocatore> giocatori;
    private Mano dealerMano;
    private HashMap<String,Integer> punteggi;

    public Stato(ArrayList<Giocatore> giocatori,Mano dealerMano,HashMap<String,Integer> punteggi){
        this.giocatori=giocatori;
        this.dealerMano=dealerMano;
        this.punteggi=punteggi;
    }

    public Giocatore getGiocatore(String playerId){
        for(Giocatore g : giocatori){
            if(g.getPlayerId() == playerId) 
                return g;
        }
        return null;
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


    @Override
    public String toString(){
        String stringStato = "";

        for(Giocatore giocatore : giocatori){
            stringStato += "Mano giocatore " + giocatore.getPlayerId() + ": " + giocatore.getMano().toString() + 
                           " punteggio: " + punteggi.get(giocatore.getPlayerId()) +"\n";
        }

        stringStato += "Mano dealer: " + dealerMano.toString() + " punteggio: " + punteggi.get("D1");

        return stringStato;
    }

}
