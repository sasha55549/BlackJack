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

        if(risposta.getStatusCode() != 100)  //Controllo che la connessione sia stata accettata
            System.exit(0);   
      
        String playerId = risposta.getPlayerId();
        Giocatore giocatore = null;

    //Partita
        out.writeObject(new Message("TURNO", playerId, giocatore));
        risposta = (Message) in.readObject();
        if(risposta.getStatusCode() != 200)
            System.exit(0);

    //INIZIO DEL MIO TURNO
        Stato statoPartita = null;
        out.writeObject(new Message("STATO", playerId, statoPartita));  //Chiedo al server le carte, che verranno inserite nell'attributo mano
        risposta = (Message) in.readObject();
        
        if(risposta.getStatusCode() != 200)  //Se la risposta è un errore rifaccio la richiesta
            System.exit(0);
        
        if(risposta.getOggetto() instanceof Stato)
            statoPartita = (Stato) risposta.getOggetto();

        giocatore = (Giocatore) null;  //Lo ricavo dalòlo stato della partita
        Mano manoDealer = null; //La ricavo poi dallo stato della partita

        //Stampa dello stato
        System.out.println(statoPartita.toString());

        System.out.println("Mano dealer: \n" + manoDealer.toString());

    //Scelta mosse
        Giocatore giocatore2 = null;
        giocata(giocatore, giocatore2, input, out, in);
        
        if(giocatore2 != null)
            giocata(giocatore2, null, input, out, in);

    //Richiesta se ho vinto o no
        out.writeObject(new Message("VITTORIA", playerId, null));
        risposta = (Message) in.readObject();
        if(risposta.getStatusCode() == 200)
            System.out.println(risposta.getOggetto().toString());  //Il server mi invierà un messaggio di vittoria
        else
            System.out.println(risposta.getOggetto().toString());  //Il server mi invierà un messaggio di sconfitta

    //Richiesta dello stato della partita
        out.writeObject(new Message("STATO", playerId, statoPartita));
        risposta = (Message) in.readObject();
        if(risposta.getStatusCode() == 200)
            System.out.println(((Stato) risposta.getOggetto()).toString());
        else   
            System.out.println("Errore");

        socket.close();
        out.close();
        in.close();
        input.close();
    }

    public static void giocata(Giocatore giocatore, Giocatore giocatore2, BufferedReader input, ObjectOutputStream out, ObjectInputStream in) throws Exception{
        String mossa = "";
        Message risposta = null;
        boolean insurance = false;

        while(!mossa.equals("STAY")){
            
            System.out.println("Tua mano: \n" + giocatore.getMano().toString());  //Stampa mano
            System.out.println("Inserisci quale mossa vuoi fare: HIT | STAI | DOUBLE | SPLIT | INSURANCE");  //Chiedo quale azione vuole eseguire e al limite la invio al server
            mossa = input.readLine();
            risposta = null;

            switch(mossa){
                case "HIT":
                    out.writeObject(new Message("HIT", giocatore.getPlayerId(), null));
                    risposta = (Message) in.readObject();  //Nel messaggio ricevo la carta che ho chiesto
                    if(risposta.getStatusCode() == 200){  //Controllo se la richiesta è andata a buon fine
                        if(risposta.getOggetto() instanceof Carta)
                            giocatore.getMano().add((Carta) risposta.getOggetto());  //Aggiungo alla mia mano la carta che mi ha dato il server
                    }
                    break;

                case "STAY":
                    out.writeObject(new Message("STAY", giocatore.getPlayerId(), giocatore));
                    risposta = (Message) in.readObject();
                    if(risposta.getStatusCode() != 200)
                        System.exit(0);
                    break;

                case "DOUBLE":
                    out.writeObject(new Message("DOUBLE", giocatore.getPlayerId(), null));
                    risposta = (Message) in.readObject();  //Nel messaggio ricevo la carta aggiuntiva
                    if(risposta.getStatusCode() == 200){  //Controllo se la richiesta è andata a buon fine
                        if(risposta.getOggetto() instanceof Carta)
                            giocatore.getMano().add((Carta) risposta.getOggetto());  //Aggiungo alla mia mano la carta che mi ha dato il server
                        mossa = "STAY";  //'Sto' in automatico
                    }
                    break;

                case "SPLIT":
                    out.writeObject(new Message("SPLIT", giocatore.getPlayerId(), null));
                    risposta = (Message) in.readObject();
                    if(risposta.getStatusCode() == 200){
                        if(risposta.getOggetto() instanceof Giocatore[]){
                            Giocatore[] giocatori = (Giocatore[]) risposta.getOggetto();
                            giocatore = giocatori[0];
                            giocatore2 = giocatori[1];
                        }
                    }
                    break;

                case "INSURANCE":
                    if(insurance){
                        System.out.println("Hai già effettuato un'insurance in precedenza");
                        break;
                    }

                    insurance = true;
                    out.writeObject(new Message("INSURANCE", giocatore.getPlayerId(), null));
                    risposta = (Message) in.readObject();
                    if(risposta.getStatusCode() != 200)
                        System.exit(0);
                    break;

                default:
                    System.out.println("Metodo inesistente");
            }

            if(risposta != null){
                if(risposta.getStatusCode() == 300){
                    System.out.println("Azione non consentita\n");
                }
            }
        }
<<<<<<< HEAD

    //Richiesta se ho vinto o no
        out.writeObject(new Message("VITTORIA", playerId, null));
        risposta = (Message) in.readObject();
        if(risposta.getStatusCode() == 200)
            System.out.println(risposta.getOggetto().toString());  //Il server mi invierà un messaggio di vittoria
        else
            System.out.println("Hai perso questo round");
        
        socket.close();
=======
>>>>>>> 3587c00e3f21c831e65f0d6e022c61a96db03792
    }
}