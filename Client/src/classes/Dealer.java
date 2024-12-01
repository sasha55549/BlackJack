package classes;

import java.util.ArrayList;

public class Dealer extends Giocatore{

    public Dealer(String playerId, Mano mano, boolean stayed){
        super(playerId, Double.POSITIVE_INFINITY, 0, mano, stayed);
    }

}