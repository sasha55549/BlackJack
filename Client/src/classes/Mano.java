package classes;
import java.io.Serializable;
import java.util.ArrayList;

public class Mano extends ArrayList<Carta> implements Serializable{
    public Mano(){}

    @Override
    public String toString(){
        String stringa = "";
        for(Carta c : this)
            stringa += c.getValore() + c.getSeme() + ", ";
        return stringa;
    }
}