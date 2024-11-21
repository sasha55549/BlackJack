import Server.classes.Message;

public class Client {
    public static final int PORT = 50000;
    public static void main(String[] args) throws Exception {
        InetAddress serverAddress = InetAddress().getByName("127.0.0.1");
        Socket socket = new Socket(serverAddress, PORT);

    //DA CAMBIARE CON I READER E WRITER PER OGGETTI
        try(BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream)); 
            PrintWriter out = new PrintWriter(socket.getOutputStream)){
            //Invio richiesta INIZIO
            out.println(new Message("INIZIO"));

            Message risposta = in.readLine();

            while(risposta.getStatusCode() == 101){
                try{
                    Thread.sleep(500);
                } catch(InterruptedException e){
                    e.printStackTrace();
                }
                out.println(new Message("INIZIO"));
                risposta = in.readLine();
            }

            if(risposta.getStatusCode() != 100)  //Controllo che la connessione sia stata accettata
                System.exit(0);

            
            
        } catch(IOException e){
            e.printStackTrace();
        }


    }
}
