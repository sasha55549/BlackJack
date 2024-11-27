import classes.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client {
    public static final int PORT = 50000;
    public static void main(String[] args) throws Exception {
        InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
        Socket socket = new Socket(serverAddress, PORT);
            
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream()); 
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        
    //Invio richiesta INIZIO
        out.writeObject(new Message("INIZIO"));

        Message risposta = (Message) in.readObject();

        while(risposta.getStatusCode() == 101){
            try{
                Thread.sleep(500);
            } catch(InterruptedException e){
                e.printStackTrace();
            }
            out.writeObject(new Message("INIZIO"));
            risposta = (Message) in.readObject();
        }

        if(risposta.getStatusCode() != 100)  //Controllo che la connessione sia stata accettata
            System.exit(0);   
      
        String playerId = risposta.getPlayerId();
        Giocatore giocatore = null;

    //Partita
        do{
            out.writeObject(new Message("TURNO", playerId, giocatore));
        } while(((Message) in.readObject()).getStatusCode() != 200);  //Finchè il server non invia la conferma del turno

    //INIZIO DEL MIO TURNO
        Stato statoPartita = new Stato();
        out.writeObject(new Message("STATO", playerId, statoPartita));  //Chiedo al server le carte, che verranno inserite nell'attributo mano
        risposta = (Message) in.readObject();
        
        if(risposta.getStatusCode() != 200)  //Se la risposta è un errore rifaccio la richiesta
            System.exit(0);

        giocatore = (Giocatore) null;  //Lo ricavo dalòlo stato della partita
        Mano manoDealer = null; //La ricavo poi dallo stato della partita

        //Stampa dello stato
        Sytstem.out.println(statoPartita.toString());

        System.out.println("Tua mano: \n" + giocatore.getMano().toString());  //Stampa mano
        System.out.println("Mano dealer: \n" + manoDealer.toString());

    //Scelta mosse
        int statoRisposta = 0;
        String mossa = "";

        while(!mossa.equals("STAY")){
        
            System.out.println("Inserisci quale mossa vuoi fare: HIT | STAI | DOUBLE | SPLIT | INSURANCE");  //Chiedo quale azione vuole eseguire e al limite la invio al server
            mossa = input.readLine();
            switch(mossa){
                case "HIT":
                    out.writeObject(new Message("HIT", playerId, giocatore));
                    statoRisposta = ((Message) in.readObject()).getStatusCode();
                    break;
                case "STAY":
                    out.writeObject(new Message("STAY", playerId, giocatore));
                    statoRisposta = ((Message) in.readObject()).getStatusCode();
                    break;
                case "DOUBLE":
                    out.writeObject(new Message("DOUBLE", playerId, giocatore));
                    statoRisposta = ((Message) in.readObject()).getStatusCode();
                    break;
                case "SPLIT":
                    out.writeObject(new Message("SPLIT", playerId, giocatore));
                    statoRisposta = ((Message) in.readObject()).getStatusCode();
                    break;
                case "INSURANCE":
                    out.writeObject(new Message("INSURANCE", playerId, giocatore));
                    statoRisposta = ((Message) in.readObject()).getStatusCode();
                    break;
                default:
                    statoRisposta = 400;
            }
            if(statoRisposta == 300){
                System.out.println("Azione non consentita\n");
            }
        }

    //Richiesta se ho vinto o no
        out.writeObject(new Message("VITTORIA", playerId, giocatore));
        if(((Message) in.readObject()).getStatusCode() == 210)
            System.out.println("Hai vinto questo round");
        else
            System.out.println("Hai perso questo round");

    //Richiesta dello stato della partita
        out.writeObject(new Message("STATO", playerId, statoPartita));
        risposta = (Message) in.readObject();
        if(risposta.getStatusCode() == 200)
            System.out.println(((Stato) risposta.getObject()).toString());
        else   
            System.out.println("Errore");

        socket.close();
        out.close();
        in.close();
        input.close();
    }
}