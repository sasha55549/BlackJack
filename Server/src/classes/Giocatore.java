package classes;

import java.net.Socket;
import java.util.ArrayList;

public class Giocatore {
    protected String playerId;
    private double bilancio;
    private int puntata;
    protected ArrayList<Carta> mano;
    protected Socket playerSocket;
    protected boolean stayed;

    public Giocatore() {
    }

    public Giocatore(String playerId, double bilancio, int puntata, ArrayList<Carta> mano, Socket playerSocket, boolean stayed) {
        this.playerId = playerId;
        this.bilancio = bilancio;
        this.puntata = puntata;
        this.mano = mano;
        this.playerSocket = playerSocket;
        this.stayed = stayed;
    }
    
    public String getPlayerId() {
        return this.playerId;
    }
    
    public void aggiungiVincita(double vincita) {
        bilancio+=vincita;
    }

    public double getBilancio() {
        return this.bilancio;
    }
    
    public int getPuntata() {
        return this.puntata;
    }
    
    public void setPuntata(int puntata) {
        this.puntata = puntata;
    }

    public void hit(Carta carta) {
        mano.add(carta);
    }
    public ArrayList<Carta> getMano() {
        return mano;
    }
    
    public Socket getPlayerSocket() {
        return this.playerSocket;
    }

    public boolean isStayed() {
        return this.stayed;
    }

    public void stay(boolean stayed) {
        this.stayed = stayed;
    }
    public void doublePlay(Carta carta) {
        setPuntata(puntata*2);
        hit(carta);
    }
    public Giocatore split() {
        Giocatore giocatore = new Giocatore(playerId+"B", bilancio, puntata, null, playerSocket, stayed);
        giocatore.hit(mano.get(0));
        mano.remove(0);
        return giocatore;
    }
    @Override
    public String toString() {
        return "{" +
            " playerId='" + getPlayerId() + "'" +
            ", bilancio='" + getBilancio() + "'" +
            ", puntata='" + getPuntata() + "'" +
            ", mano='" + getMano() + "'" +
            ", playerSocket='" + getPlayerSocket() + "'" +
            ", stayed='" + isStayed() + "'" +
            "}";
    }

}
