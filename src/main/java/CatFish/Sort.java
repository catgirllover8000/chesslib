package CatFish;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.PieceType;
import com.github.bhlangonijr.chesslib.move.Move;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

class mvvlvaComparator implements Comparator<Move>{

    HashMap<PieceType,Integer> materialValue = new HashMap<>();

    Board board;

    public mvvlvaComparator(Board board){
        this.board = board;
        materialValue.put(PieceType.PAWN, 100);
        materialValue.put(PieceType.KNIGHT, 320);
        materialValue.put(PieceType.BISHOP, 330);
        materialValue.put(PieceType.ROOK, 500);
        materialValue.put(PieceType.QUEEN, 900);
        materialValue.put(PieceType.KING, 10000);
    }
    //@Override
    public int compare(Move m1, Move m2){
        PieceType moving1 = board.getPiece(m1.getFrom()).getPieceType();
        PieceType moving2 = board.getPiece(m2.getFrom()).getPieceType();
        PieceType taken1 = board.getPiece(m1.getTo()).getPieceType();
        PieceType taken2 = board.getPiece(m2.getTo()).getPieceType();

        if (taken1 == null && taken2 == null){
            return 0;
        }

        if (taken1 == null){
            return 10000;
        }
        if (taken2 == null){
            return -10000;
        }
        int value1 = materialValue.get(moving1) - materialValue.get(taken1);
        int value2 = materialValue.get(moving2) - materialValue.get(taken2);

        return (value1 - value2);

    }
}
public class Sort {

    public List<Move> MVVLVA(List<Move> moves, Board board) {
        moves.sort(new mvvlvaComparator(board));
        return moves;
        // Own sorting alg (merge/quick sort?)probably magnitudes faster
    }
}
