import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Client {
    public static final int PORT = 50000;
    public static void main(String[] args) throws Exception {
        InetAddress serverAddress = InetAddress().getByName("127.0.0.1");
        Socket socket = new Socket(serverAddress, PORT);
            
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream()); 
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
        BufferedReader input = new BufferedReader(System.in);
        
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

        Giocatore giocatore = new Giocatore();
    //Partita
        out.writeObject(new Message("FINE", plyerId, giocatore));
        while(in.readObject().getStatusCode() == 201){  //Finché non ho finito di giocare  chiedo al server se è il mio turno
            do{
                out.writeObject(new Message("TURNO", playerId, giocatore));
            } while(in.readObject().getStatusCode != 200);  //Finchè il server non invia la conferma del turno

        //INIZIO DEL MIO TURNO
            out.writeObject(new Message("MANO", playerId, giocatore));  //Chiedo al server le carte, che verranno inserite nell'attributo mano
            risposta = in.readObject();
            
            if(risposta.getStatusCode() == 400)  //Se la risposta è un errore rifaccio la richiesta
                System.exit(0);
            giocatore = risposta.getOggetto();

            System.out.println("Tua mano: \n" + giocatore.getMano().toString());  //Stampa mano
            Mano manoDealer = new Mano();
            out.writeObject("MANO", playerId, manoDealer);
            System.out.println("Mano dealer: \n" + manoDealer.toString());

            int statoRisposta = 0;
            while(statoRisposta != 200){
            //Chiedo quale azione vuole eseguire e al limite la invio al server
                System.out.println("Inserisci quale mossa vuoi fare: CARTA | STAI | RADDOPPIO | SPLIT | ASSICURAZIONE");
                String mossa = input.readLine();
                switch(mossa){
                    case "CARTA":
                        out.writeObject(new Message("HIT", playerId, giocatore));
                        statoRisposta = in.readObject().getStatusCode();
                        break;
                    case "STAI":
                        out.writeObject(new Message("STAI", playerId, giocatore));
                        statoRisposta = in.readObject().getStatusCode();
                        break;
                    case "RADDOPPIO":
                        out.writeObject(new Message("RADDOPPIO", playerId, giocatore));
                        statoRisposta = in.readObject().getStatusCode();
                        break;
                    case "SPLIT":
                        out.writeObject(new Message("SPLIT", playerId, giocatore));
                        statoRisposta = in.readObject().getStatusCode();
                        break;
                    case "ASSICURAZIONE":
                        out.writeObject(new Message("ASSICURAZIONE", playerId, giocatore));
                        statoRisposta = in.readObject().getStatusCode();
                        break;
                    default:
                        statoRisposta = 400;
                }
            }

            out.writeObject(new Message("FINE", plyerId, giocatore));  //Chiedo al server se non posso più fare mosse
        }

        out.writeObject(new Message("VITTORIA", playerId, giocatore));
        if(in.readObject().getStatusCode == 210)
            System.out.println("Hai vinto questo round");
        else
            System.out.println("Hai perso questo round");
    }
}
