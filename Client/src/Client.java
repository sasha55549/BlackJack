import Server.classes.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Client {
    public static final int PORT = 50000;
    public static void main(String[] args) throws Exception {
        InetAddress serverAddress = InetAddress().getByName("127.0.0.1");
        Socket socket = new Socket(serverAddress, PORT);

    //DA CAMBIARE CON I READER E WRITER PER OGGETTI
        // try(ObjectInputStream in = new ObjectInputStream(new InputStreamReader(socket.getInputStream)); 
        //     ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream)){
            
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream()); 
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
            
        //Invio richiesta INIZIO
            out.writeObject(new Message("INIZIO"));

            Message risposta = (Message) in.readObject();

            while(risposta.getStatusCode() == 101){
                try{
                    Thread.sleep(500);
                } catch(InterruptedException e){
                    e.printStackTrace();
                }
                out.println(new Message("INIZIO"));
                risposta = (Message) in.readObject();
            }

            if(risposta.getStatusCode() != 100)  //Controllo che la connessione sia stata accettata
                System.exit(0);
            String playerId = risposta.getPlayerId();

        //Partita
            while(true){
                do{
                    out.writeObject(new Message("TURNO", playerId));
                } while(in.readObject().getStatusCode != 200);  //Finchè il server non invia la conferma del turno

            //INIZIO DEL MIO TURNO
                Mano mano = new Mano();
                out.writeObject(new Message("INIZIO_TURNO", playerId, mano)); 
                risposta = in.readObject();
                
                if(risposta.getStatusCode() == 404)  //Se la risposta è un errore rifaccio la richiesta
                    System.exit(0);
                mano = risposta.getOggetto();

                System.out.println(mano.toString);
            //Chiedo quale azione vuole eseguire e al limite la invio al server
                System.out.println();
            }
        // } catch(IOException e){
        //     e.printStackTrace();
        // }


    }
}