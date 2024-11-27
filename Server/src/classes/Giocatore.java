package classes;

import java.net.Socket;

public class Giocatore {
    protected String playerId;
    protected double bilancio;
    protected int puntata;
    protected Mano mano;
    protected Socket playerSocket;
    protected boolean stayed;
    private boolean insurance;

    public Giocatore() {
    }

    public Giocatore(String playerId, double bilancio, int puntata, Mano mano, Socket playerSocket, boolean stayed) {
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
    public Mano getMano() {
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
        bilancio -= puntata;
        hit(carta);
    }
    public Giocatore split() {
        bilancio -= puntata;
        puntata*=2;
        Giocatore giocatore = new Giocatore(playerId+"B", 0, 0, null, playerSocket, stayed);
        giocatore.hit(mano.remove(0));
        return giocatore;
    }
    public void insurance() {
        bilancio -= puntata/2;
        puntata+=puntata/2;
        insurance = true;
    }
    public boolean getInsurance() {
        return insurance;
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