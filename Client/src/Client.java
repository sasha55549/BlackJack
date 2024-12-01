import classes.*;
import services.ClientService;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client {
    public static final int PORT = 50000;
    private ClientService clientService;
    public static void main(String[] args) throws Exception {
        Client client = new Client();
        String serverAddress = "localhost";
        Socket socket = new Socket(serverAddress, PORT);
        System.out.println("Connesso al server");
            
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream()); 
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        client.clientService = new ClientService(socket, in, out);
        

    //Invio richiesta INIZIO
        client.clientService.sendMessage(new Message("INIZIO"));

        Object rispostaObj = client.clientService.recieveMessage();
        Message risposta = null;
        if(rispostaObj instanceof Message) {
            risposta = (Message) rispostaObj;
        }
        
        if(risposta.getStatusCode() != 200)  //Controllo che la connessione sia stata accettata
            System.out.println("Errore inviato dal server");  // System.out.println("StatusCode richiesta INIZIO: " + risposta.getStatusCode());
      
        String playerId = risposta.getPlayerId();  
        System.out.println("Sei il giocatore " + playerId);
        
    //Partita   
        Giocatore giocatore = new Giocatore();
        out.writeObject(new Message("TURNO", playerId, giocatore));
        
        risposta = (Message) in.readObject();
        if(risposta.getStatusCode() != 200)
            System.out.println("Errore inviato dal server");
        giocatore = (Giocatore) risposta.getOggetto();

    //INIZIO DEL MIO TURNO
        Stato statoPartita = new Stato(null, null, null);
        out.writeObject(new Message("STATO", playerId, statoPartita));  //Chiedo al server le carte, che verranno inserite nell'attributo mano
        
        risposta = (Message) in.readObject();  //System.out.println("Risposta di STATO: " + risposta.getStatusCode());
        
        if(risposta.getStatusCode() != 200)  //Se la risposta è un errore rifaccio la richiesta
            System.out.println("Errore inviato dal server");
        
        if(risposta.getOggetto() instanceof Stato)
            statoPartita = (Stato) risposta.getOggetto();

        // dealerMano = statoPartita.getDealerMano();

        //Stampa dello stato della partita
        System.out.println(statoPartita.toString());

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

        System.out.println("\nTua mano: \n" + giocatore.getMano().toString());  //Stampa mano
        while(!giocatore.isStayed()){
            
            System.out.println("Inserisci quale mossa vuoi fare: HIT | STAY | DOUBLE | SPLIT | INSURANCE");  //Chiedo quale azione vuole eseguire e al limite la invio al server
            mossa = input.readLine().toUpperCase();
            risposta = null;

            switch(mossa){
                case "HIT":
                    out.writeObject(new Message("HIT", giocatore.getPlayerId(), null));
                    risposta = (Message) in.readObject();  //Nel messaggio ricevo la carta che ho chiesto
                    if(risposta.getStatusCode() == 200){  //Controllo se la richiesta è andata a buon fine
                        if(risposta.getOggetto() instanceof Carta){
                            giocatore.getMano().add((Carta) risposta.getOggetto());  //Aggiungo alla mia mano la carta che mi ha dato il server
                            out.writeObject(new Message("PUNTEGGIO", giocatore.getPlayerId(), null));
                            
                            Message rispostaPunteggio = (Message) in.readObject();
                            Integer punteggio = (Integer) rispostaPunteggio.getOggetto();
                            if(punteggio >= 21)  //Se ho 21 o ho sballato 'sto' in automatico
                                giocatore.stay(true);
                        }
                    }
                    break;

                case "STAY":
                    out.writeObject(new Message("STAY", giocatore.getPlayerId(), giocatore));
                    risposta = (Message) in.readObject();
                    if(risposta.getStatusCode() != 200)
                        System.out.println("Errore del server dopo richiesta STAY");
                    giocatore.stay(true);
                    break;

                case "DOUBLE":
                    out.writeObject(new Message("DOUBLE", giocatore.getPlayerId(), null));
                    risposta = (Message) in.readObject();  //Nel messaggio ricevo la carta aggiuntiva
                    if(risposta.getStatusCode() == 200){  //Controllo se la richiesta è andata a buon fine
                        if(risposta.getOggetto() instanceof Carta)
                            giocatore.getMano().add((Carta) risposta.getOggetto());  //Aggiungo alla mia mano la carta che mi ha dato il server
                        giocatore.stay(true);//'Sto' in automatico
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
                        System.out.println("Non puoi effettuare un INSURANCE in questo momento");
                    break;

                default:
                    System.out.println("Metodo inesistente");
            }

            if(risposta != null){
                if(risposta.getStatusCode() == 300){
                    System.out.println("Azione non consentita\n");
                }
            }
            
            System.out.println("\nTua mano: \n" + giocatore.getMano().toString());  //Stampa mano           
        }
    }
}