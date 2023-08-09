package CatFish;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;
import java.util.List;


public class Engine {

    public static final int MIN = -100000;
    public static final int MAX = 100000;

    Eval eval = new Eval();
    Boolean AIPlayer;

    public Engine(Side AIPlayer){
        this.AIPlayer = AIPlayer == Side.WHITE;
    }

    public Move pickMove(Board board, int depth){
        if (AIPlayer && board.getSideToMove() == Side.WHITE){
            Move best_move = null;
            int best_value = MIN;
            for (Move move : board.legalMoves()){
                board.doMove(move);
                int value = Search(board, depth - 1, MIN, MAX);
                board.undoMove();
                if (value > best_value){
                    best_value = value;
                    best_move = move;
                }
            }
            return best_move;
        } else if ((!AIPlayer) && board.getSideToMove() == Side.BLACK){
            Move worst_move = null;
            int worst_value = MAX;
            for (Move move : board.legalMoves()){
                board.doMove(move);
                int value = Search(board, depth - 1, MIN, MAX);
                board.undoMove();
                if (value < worst_value){
                    worst_value = value;
                    worst_move = move;
                }
            }
            return worst_move;
        } else {
            System.out.println("Inconsistency in side selection");
        }
        return null;
    }

/*    public int Quiesce(Board board, int alpha, int beta, int maxdepth){
        if (maxdepth == 0){
            return eval.evaluate(board);
        }
        int eval = this.eval.evaluate(board);
        if (eval >= beta){
            return beta;
        }
        if (alpha < eval){
            alpha = eval;
        }

        for (Move move : board.legalMoves()){
            if (board.getPiece(move.getTo()) != null){
                board.doMove(move);
                int score = -Quiesce(board, alpha, beta, maxdepth - 1);
                board.undoMove();
                if (score >= beta){
                    return beta;
                }
                if (score > alpha){
                    alpha = score;
                }
            }
        }
        return alpha;
    }*/


    public int Search(Board board, int depth, int alpha, int beta) {
        if (depth == 0 || board.isDraw() || board.isMated()){
            return eval.evaluate(board);//Quiesce(board, alpha, beta, 5);
        }
        List<Move> legalMoves = board.legalMoves();

        if (board.getSideToMove() == Side.WHITE){
            Move best_move;
            int best_value = MIN;

            for (Move move : legalMoves){
                board.doMove(move);
                int value = Search(board, depth -1, alpha, beta);
                best_value = Math.max(value, best_value);
                board.undoMove();
                alpha = Math.max(alpha, best_value);
                if (beta <= alpha){
                    break;
                }
            }
            return best_value;
        } else {
            Move worst_move;
            int worst_value = MAX;

            for (Move move : legalMoves){
                board.doMove(move);
                int value = Search(board, depth -1, alpha, beta);
                worst_value = Math.min(value, worst_value);
                board.undoMove();
                beta = Math.min(beta, value);
                if (beta <= alpha){
                    break;
                }
            }
            return worst_value;
        }
    }
}
