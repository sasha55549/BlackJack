package classes;

import java.io.Serializable;
import java.util.Objects;

public class Message implements Serializable{
    private static final long serialVersionUID = 1L;
    private int statusCode;
    private String playerId;
    private Object oggetto;

    public Message() {
    }

    public Message(int statusCode, String playerId, Object oggetto) {
        this.statusCode = statusCode;
        this.playerId = playerId;
        this.oggetto = oggetto;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getMessage() {
        return this.message;
    }

    public Object getOggetto() {
        return this.oggetto;
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

    public String toString() {
        return "{" +
            " statusCode='" + getStatusCode() + "'" +
            ", message='" + getMessage() + "'" +
            ", oggetto='" + getOggetto() + "'" +
            "}";
    }
}
