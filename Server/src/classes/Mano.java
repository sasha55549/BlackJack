import java.util.ArrayList;

public class Mano extends ArrayList<Carta>{
    public Mano(){}

    @Override
    public String toString(){
        String stringa = "";
        for(Carta c : this)
            stringa += c.getValore() + c.getSeme();
        return stringa;
    }
}