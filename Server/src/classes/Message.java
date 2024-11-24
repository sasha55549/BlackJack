package classes;

import java.io.Serializable;

public class Message implements Serializable{
    private static final long serialVersionUID = 1L;
    private int statusCode;
    private String playerId;
    private Object oggetto;
    private String method;
    private String playerId;

    public Message() {
    }
//Response
    public Message(int statusCode, String playerId, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
    public Message(int statusCode, String playerId, Object oggetto) {
        this.statusCode = statusCode;
        this.playerId = playerId;
        this.oggetto = oggetto;
    }

//Request
    public Message(String method){
        this.method = method;
    }

    public Message(String method, String playerId, Object oggetto){
        this.method = method;
        this.oggetto = oggetto;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public Object getOggetto() {
        return this.oggetto;
    }

    public String getMethod(){
        return this.method;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
    public void setOggetto(Object oggetto) {
        this.oggetto = oggetto;
    }


    @Override
    public String toString() {
        return "{" +
            " statusCode='" + getStatusCode() + "'" +
            ", playerId='" + getPlayerId() + "'" +
            ", oggetto='" + getOggetto() + "'" +
            "}";
    }
    
}
