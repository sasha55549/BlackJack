package classes;

public class Carta {

    static final String[] VALORI = new String[]{"2","3","4","5","6","7","8","9","10","J","Q","K","A"};
    static final String[] SEMI = new String[]{"F","Q","P","C"};

    private String valore;
    private String seme;


    public Carta() {
    }

    public Carta(String valore, String seme) {
        this.valore = valore;
        this.seme = seme;
    }

    public String getValore() {
        return this.valore;
    }

    public void setValore(String valore) {
        this.valore = valore;
    }

    public String getSeme() {
        return this.seme;
    }

    public void setSeme(String seme) {
        this.seme = seme;
    }

    @Override
    public String toString() {
        return "{" +
            " valore='" + getValore() + "'" +
            ", seme='" + getSeme() + "'" +
            "}";
    }
    
}
