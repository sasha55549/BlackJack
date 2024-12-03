import classes.*;
import services.ClientService;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;

//TODO richiesta turno in polling
//TODO invio puntata
//TODO gestione FINE
//TODO richiesta VITTORIA 
//TODO insurance aggiunto a giocatore

public class Client {
    public static final int PORT = 50000;
    private ClientService clientService;
    @SuppressWarnings("unused")
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

    //INIZIO DEL MIO TURNO
        Stato statoPartita = new Stato(null, null, null);
        client.clientService.sendMessage(new Message("STATO", playerId, statoPartita));  //Chiedo al server le carte, che verranno inserite nell'attributo mano
        
        risposta = (Message) client.clientService.recieveMessage();  //System.out.println("Risposta di STATO: " + risposta.getStatusCode());
        
        if(risposta.getStatusCode() != 200)  //Se la risposta è un errore rifaccio la richiesta
            System.out.println("Errore inviato dal server");
        
        if(risposta.getOggetto() instanceof Stato)
            statoPartita = (Stato) risposta.getOggetto();

        // dealerMano = statoPartita.getDealerMano();

        //Stampa dello stato della partita
        System.out.println(statoPartita.toString());

    //Partita   
        Giocatore giocatore = new Giocatore();
                
        client.clientService.sendMessage(new Message("TURNO", playerId, null));
        risposta = (Message) client.clientService.recieveMessage();
        while(risposta.getStatusCode() != 200){
            // System.out.println("Status code richiesta turno: "risposta.getStatusCode());
            try {
                Thread.sleep(100 + (int) Math.floor(Math.random() * 400));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            client.clientService.sendMessage(new Message("TURNO", playerId, null));
            risposta = (Message) client.clientService.recieveMessage();
        }

        giocatore = (Giocatore) risposta.getOggetto();

    //Scelta mosse
        Giocatore giocatore2 = null;
        giocata(giocatore, giocatore2, input, out, in, client);
        
        if(giocatore2 != null){
            client.clientService.sendMessage(new Message("TURNO", playerId, null));
            risposta = (Message) client.clientService.recieveMessage();
            while(risposta.getStatusCode() != 200){
                // System.out.println("Status code richiesta turno: "risposta.getStatusCode());
                try {
                    Thread.sleep(100 + (int) Math.floor(Math.random() * 400));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                client.clientService.sendMessage(new Message("TURNO", playerId, null));
                risposta = (Message) client.clientService.recieveMessage();
            }
            giocata(giocatore2, null, input, out, in, client);
        }


        client.clientService.sendMessage(new Message("FINE", playerId, null));
        risposta = (Message) client.clientService.recieveMessage();
        while(risposta.getStatusCode() != 200){
            try {
                Thread.sleep(100 + (int) Math.floor(Math.random() * 400));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            client.clientService.sendMessage(new Message("FINE", playerId, null));
            risposta = (Message) client.clientService.recieveMessage();
        }

    //Richiesta se ho vinto o no
        client.clientService.sendMessage(new Message("VITTORIA", playerId, null));
        risposta = (Message) client.clientService.recieveMessage();
        if(risposta.getStatusCode() == 200)
            System.out.println(risposta.getOggetto().toString());  //Il server mi invierà un messaggio di vittoria
        else if(risposta.getOggetto().toString() != null)
            System.out.println(risposta.getOggetto().toString());  //Il server mi invierà un messaggio di sconfitta

    //Richiesta dello stato della partita
        client.clientService.sendMessage(new Message("STATO", playerId, statoPartita));
        risposta = (Message) client.clientService.recieveMessage();
        if(risposta.getStatusCode() == 200)
            System.out.println(((Stato) risposta.getOggetto()).toString());
        else   
            System.out.println("Errore");

        socket.close();
        out.close();
        in.close();
        input.close();
    }

    public static void giocata(Giocatore giocatore, Giocatore giocatore2, BufferedReader input, ObjectOutputStream out, ObjectInputStream in, Client client) throws Exception{
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
                    client.clientService.sendMessage(new Message("HIT", giocatore.getPlayerId(), null));
                    risposta = (Message) client.clientService.recieveMessage();  //Nel messaggio ricevo la carta che ho chiesto
                    if(risposta.getStatusCode() == 200){  //Controllo se la richiesta è andata a buon fine
                        if(risposta.getOggetto() instanceof Carta){
                            giocatore.getMano().add((Carta) risposta.getOggetto());  //Aggiungo alla mia mano la carta che mi ha dato il server
                            client.clientService.sendMessage(new Message("PUNTEGGIO", giocatore.getPlayerId(), null));
                            
                            Message rispostaPunteggio = (Message) client.clientService.recieveMessage();
                            Integer punteggio = (Integer) rispostaPunteggio.getOggetto();
                            if(punteggio >= 21)  //Se ho 21 o ho sballato 'sto' in automatico
                                giocatore.stay();
                        }
                    }
                    break;

                case "STAY":
                    client.clientService.sendMessage(new Message("STAY", giocatore.getPlayerId(), giocatore));
                    risposta = (Message) client.clientService.recieveMessage();
                    if(risposta.getStatusCode() != 200)
                        System.out.println("Errore del server dopo richiesta STAY");
                    giocatore.stay();
                    break;

                case "DOUBLE":
                    client.clientService.sendMessage(new Message("DOUBLE", giocatore.getPlayerId(), null));
                    risposta = (Message) client.clientService.recieveMessage();  //Nel messaggio ricevo la carta aggiuntiva
                    if(risposta.getStatusCode() == 200){  //Controllo se la richiesta è andata a buon fine
                        if(risposta.getOggetto() instanceof Carta)
                            giocatore.getMano().add((Carta) risposta.getOggetto());  //Aggiungo alla mia mano la carta che mi ha dato il server
                        giocatore.stay();//'Sto' in automatico
                    }
                    break;

                case "SPLIT":
                    client.clientService.sendMessage(new Message("SPLIT", giocatore.getPlayerId(), null));
                    risposta = (Message) client.clientService.recieveMessage();
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

                    client.clientService.sendMessage(new Message("INSURANCE", giocatore.getPlayerId(), null));
                    risposta = (Message) client.clientService.recieveMessage();
                    if(risposta.getStatusCode() != 200)
                        System.out.println("Non puoi effettuare un INSURANCE in questo momento");
                    insurance = true;
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