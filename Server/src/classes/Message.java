package classes;

import java.io.Serializable;

public class Message implements Serializable{
    private static final long serialVersionUID = 1L;
    private int statusCode;
    private String message;
    private Object oggetto;
    private String method;

    public Message() {

    }
    public Message(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
    public Message(int statusCode, Object oggetto) {
        this.statusCode = statusCode;
        this.oggetto = oggetto;
    }

    public Message(String method){
        this.method = method;
    }

    public Message(String method, Object oggetto){
        this.method = method;
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

    public String getMethod(){
        return this.method;
    }

    public String toString() {
        return "{" +
            " statusCode='" + getStatusCode() + "'" +
            ", message='" + getMessage() + "'" +
            ", oggetto='" + getOggetto() + "'" +
            "}";
    }
}
