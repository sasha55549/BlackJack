package controllers;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import classes.Carta;
import classes.Dealer;
import classes.Giocatore;
import classes.Mano;
import classes.Message;
import classes.Stato;
import services.ClientCommunicationService;

public class PartitaController extends Thread {
    private ArrayList<Giocatore> giocatori;
    private HashMap<String,Giocatore> giocatori2;
    private ArrayList<Carta> mazzo;
    private Dealer dealer;
    private HashMap<String,Integer> punteggi;
    private HashMap<Giocatore,ClientCommunicationService> connections;
    private String currentPlayer;
    private Iterator<Giocatore> i;
    static int playersNumber = 0;
    private ArrayList<ObjectInputStream> inList;
    private ArrayList<ObjectOutputStream> outList;
    private ArrayList<Socket> sockets;
    private boolean fine;

    public PartitaController (ArrayList<Socket> sockets, ArrayList<ObjectInputStream> inList, ArrayList<ObjectOutputStream> outList){
        this.giocatori = new ArrayList<Giocatore>();
        this.giocatori2 = new HashMap<String,Giocatore>();
        this.punteggi = new HashMap<String,Integer>();
        this.connections = new HashMap<Giocatore,ClientCommunicationService>();
        this.mazzo = new ArrayList<Carta>();
        this.inList = inList;
        this.outList = outList;
        this.sockets = sockets;
        
        this.dealer = new Dealer("D1",new Mano(),false);
        this.punteggi.put("D1",0);

        int count=0;

        for (Socket socket : sockets) {
            Giocatore giocatore = new Giocatore("P"+Integer.toString(++playersNumber), 1000,0, new Mano(), false);
            this.giocatori.add(giocatore);
            this.giocatori2.put("P"+Integer.toString(playersNumber), giocatore);
            this.connections.put(giocatore, new ClientCommunicationService(this, socket, true, giocatore, dealer, inList.get(count), outList.get(count)));
            this.punteggi.put("P"+Integer.toString(playersNumber), 0);
            count++;
        }
        
        this.i= giocatori.iterator();
    }

    private void connessioni(){
        for (Giocatore giocatore : connections.keySet()) {
            connections.get(giocatore).start();
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

    private synchronized boolean allPlayersStayed(){
        boolean allPlayersStayed = true;
        for (Giocatore giocatore : this.giocatori) {
            if (!giocatore.isStayed()) {
                 allPlayersStayed=false;
                 break;
            }
        }
        return allPlayersStayed;
    }

    private synchronized void calcolaPunteggi(){
            for (Giocatore giocatore : giocatori) {
                int p=0, assi=0;
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
                            p+=11;
                            assi++;
                            break;
                        default:
                            p+=Integer.parseInt(valore);
                            break;
                    }
                    while(p>21 && assi>0) {
                        p-=10;
                        assi--;
                    }
                    punteggi.put(giocatore.getPlayerId(), p);
                }
            }
            int dealerManoSize=dealer.getMano().size();
            if (!allPlayersStayed() && dealerManoSize==2) dealerManoSize-=1; //non fa vedere il punteggio inclusa la carta coperta
            int p=0;
            int assi=0;
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
                            p+=11;
                            assi++;
                            break;
                        default:
                            p+=Integer.parseInt(valore);
                            break;
                    }
                    while(p>21 && assi>0) {
                        p-=10;
                        assi--;
                    }
                    punteggi.put(dealer.getPlayerId(), p);
            }
            
        }

    public synchronized boolean getFine(){
        return fine;
    }

    private synchronized void setFine(boolean fine){
        this.fine=fine;
    }

    public synchronized String getCurrentPlayer(){
        return currentPlayer;
    }

    public synchronized Message turno(Message message) {
        String method = message.getMethod();
        Giocatore giocatore = giocatori2.get(message.getPlayerId());
        switch (method) {
            case "FINE":
                if (getFine()) return new Message(200, giocatore.getPlayerId());
                else return new Message(300,giocatore.getPlayerId());

            case "PUNTATA":
                if (giocatore.getPuntata()==0 && ((Integer)message.getOggetto())<=giocatore.getBilancio()) {
                        giocatore.setPuntata((Integer)message.getOggetto());
                        giocatore.setBilancio(giocatore.getBilancio()-giocatore.getPuntata());
                        return new Message(200,giocatore.getPlayerId());
                }
                else return new Message(300,giocatore.getPlayerId());
            case "STATO":
                Mano dealerMano = (Mano)dealer.getMano().clone();
                if (!allPlayersStayed()) dealerMano.RemoveLast();
                return new Message(200,message.getPlayerId(),new Stato(giocatori,dealerMano,(HashMap<String,Integer>)punteggi.clone()));

            case "HIT":
                if (message.getPlayerId().equalsIgnoreCase(currentPlayer) && !giocatore.isStayed() && punteggi.get(giocatore.getPlayerId())<21) {
                    Carta carta = mazzo.remove(0);
                    giocatore.hit(carta);
                    calcolaPunteggi();
                    if (punteggi.get(giocatore.getPlayerId())>=21) {
                        giocatore.stay();
                        if (i.hasNext()) currentPlayer = i.next().getPlayerId();
                    }
                    return new Message(200,giocatore.getPlayerId(), carta);
                }
                return new Message(300, giocatore.getPlayerId());

            case "PUNTEGGIO":
                calcolaPunteggi();
                return new Message(200, giocatore.getPlayerId(), punteggi.get(giocatore.getPlayerId()));

            case "STAY":
                if (message.getPlayerId().equalsIgnoreCase(currentPlayer) && !giocatore.isStayed() && punteggi.get(giocatore.getPlayerId())<=21) {
                giocatore.stay();
                if (i.hasNext()) currentPlayer = i.next().getPlayerId();
                return new Message(200, giocatore.getPlayerId());
                }
                return new Message(300,giocatore.getPlayerId());

            case "DOUBLE":
                if(message.getPlayerId().equalsIgnoreCase(currentPlayer) && !giocatore.isStayed() && punteggi.get(giocatore.getPlayerId())<21 && giocatore.getBilancio()-giocatore.getPuntata()>=0 && giocatore.getMano().size()==2) {
                    Carta carta = mazzo.remove(0);
                    giocatore.doublePlay(carta);
                    calcolaPunteggi();
                    giocatore.stay();
                    if (i.hasNext()) currentPlayer = i.next().getPlayerId();
                    return new Message(200, giocatore.getPlayerId(), carta);
                }
                return new Message(300, giocatore.getPlayerId());

            case "SPLIT":
                if(message.getPlayerId().equalsIgnoreCase(currentPlayer) && !giocatore.isStayed() && punteggi.get(giocatore.getPlayerId())<21 && giocatore.getBilancio()-giocatore.getPuntata()>=0 && (giocatore.getMano().get(0).getValore() == giocatore.getMano().get(1).getValore())) {
                    Giocatore split = giocatore.split();
                    giocatori.add(giocatori.indexOf(giocatore)+1, split);
                    giocatori2.put(split.getPlayerId(), split);
                    connections.put(split, new ClientCommunicationService(this, sockets.get(giocatori.indexOf(giocatore)), true, split, dealer, connections.get(giocatore).getIn(), connections.get(giocatore).getOut()));
                    giocatore.hit(mazzo.remove(0));
                    split.hit(mazzo.remove(0));
                    calcolaPunteggi();
                    /*if(punteggi.get(giocatore.getPlayerId())>21) {
                        giocatore.stay();
                    }
                    if(punteggi.get(split.getPlayerId())>21) {
                        split.stay();
                    }*/
                    Giocatore[] giocatoriTemp = {giocatore, split};
                    return new Message(200, giocatore.getPlayerId(), giocatoriTemp);

                }
                break;

            case "INSURANCE":
            if(message.getPlayerId().equalsIgnoreCase(currentPlayer) && dealer.getMano().get(0).getValore().equals("A") && giocatore.getMano().size()==2 && !giocatore.isStayed() && giocatore.getBilancio() - (giocatore.getPuntata()/2)>=0) {
                giocatore.insurance();
                return new Message(200, giocatore.getPlayerId());
            }
            return new Message(300, giocatore.getPlayerId());

            case "VITTORIA":
                    switch (verificaVincita(giocatore)) {
                        case 1:
                            return new Message(211, giocatore.getPlayerId(), calcolaVincita(giocatore));
                        case 0:
                            return new Message(210, giocatore.getPlayerId(), calcolaVincita(giocatore));
                        case -1:
                            return new Message(209, giocatore.getPlayerId(), calcolaVincita(giocatore));
                        default:
                            break;
                    }
                   
                break;

            default:
                break;
        }
        return null;
    }

    private boolean hasBlackjack(Giocatore giocatore){
        boolean blackjack = false;
        if (punteggi.get(giocatore.getPlayerId())==21 && giocatore.getMano().size()==2) blackjack=true;
        return blackjack;
    }

    private int verificaVincita(Giocatore giocatore){
        int vincita = 1;
            if (!fine || (punteggi.get(giocatore.getPlayerId())<punteggi.get(dealer.getPlayerId()) && punteggi.get(dealer.getPlayerId())<=21) || punteggi.get(giocatore.getPlayerId())>21) return -1;
            if ((punteggi.get(giocatore.getPlayerId())>punteggi.get(dealer.getPlayerId()) && punteggi.get(giocatore.getPlayerId())<=21 ) || (hasBlackjack(giocatore) && !hasBlackjack(dealer))) return 1;
            if (punteggi.get(giocatore.getPlayerId())==punteggi.get(dealer.getPlayerId()) || (hasBlackjack(giocatore) && hasBlackjack(dealer))) return 0;
        
        return vincita;
    }

    private double calcolaVincita(Giocatore giocatore){
        double puntata =  giocatore.getPuntata();
        double vincita = 0;
        double insurance = 0;
        if (giocatore.getInsurance()) {
            insurance=puntata/3;
            puntata-=insurance;
            if (hasBlackjack(dealer)) {
                vincita=insurance*3;
                if (hasBlackjack(giocatore)) vincita+=puntata;
                return vincita;
            }
        } 
            if (hasBlackjack(giocatore) && !hasBlackjack(dealer)) {
                vincita=puntata+puntata*3/2;
                return vincita;
            }
            if ((punteggi.get(giocatore.getPlayerId())==punteggi.get(dealer.getPlayerId()) && punteggi.get(dealer.getPlayerId())<=21) || (hasBlackjack(giocatore) && hasBlackjack(dealer))) {
                vincita=puntata;
                return vincita;
            }
            if (punteggi.get(giocatore.getPlayerId())>punteggi.get(dealer.getPlayerId()) && punteggi.get(giocatore.getPlayerId())<=21 || (punteggi.get(dealer.getPlayerId())>21 && punteggi.get(giocatore.getPlayerId())<=21)) {
                vincita=puntata*2;
                return vincita;
            }
        return vincita;
    }

    private void distribuisciCarte(){
        for (Giocatore giocatore : giocatori) {
            giocatore.hit(mazzo.remove(0));
        }
        dealer.hit(mazzo.remove(0));
        for (Giocatore giocatore : giocatori) {
            giocatore.hit(mazzo.remove(0));
        }
        dealer.hit(mazzo.remove(0));
    }
    @Override
    public void run(){
        currentPlayer = i.next().getPlayerId();
        generaMazzo();
        mischiaMazzo();
        distribuisciCarte();
        calcolaPunteggi();
        connessioni();   
        System.out.println("prova1");
        boolean allPlayersStayed = allPlayersStayed();
        while (!allPlayersStayed) {
            allPlayersStayed = allPlayersStayed();
            }
        System.out.println("prova3");
        while (punteggi.get(dealer.getPlayerId())<17) {
            dealer.hit(mazzo.remove(0));
            calcolaPunteggi();
        }
        setFine(true);
        while (getFine()) {
        }
    }
    
}