package CatFish;

import com.github.bhlangonijr.chesslib.move.Move;

public class TTentry {

    private final Move PV;
    private final int bestValue;
    private final int depth;

    public TTentry(Move PV, int bestValue, int depth){
        this.PV = PV;
        this.bestValue = bestValue;
        this.depth = depth;
    }

    public Move getPV() {
        return PV;
    }

    public int getBestValue(){
        return bestValue;
    }

    public int getDepth(){
        return depth;
    }
}
