package CatFish;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;

import java.util.*;

import static CatFish.Eval.isEndgame;


public class Engine {

    public static int nodes;

    public static final int MIN = -100000;
    public static final int MAX = 100000;
    Eval eval = new Eval();
    Sort sort = new Sort();
    Map<Long, TTentry> tt = new HashMap<Long, TTentry>();
    Side AIPlayer;

    int R = 2;

    public Engine(Side AIPlayer){
        this.AIPlayer = AIPlayer;
    }

/*
    private List<Move> MVVLVA(List<Move> moves){

    }
*/

    public Move pickMove(Board board, int depth){
        tt.clear();
        nodes = 0;
        if (AIPlayer == Side.WHITE && board.getSideToMove() == Side.WHITE){
            Move best_move = null;
            int best_value = MIN;
            for (Move move : board.legalMoves()){
                board.doMove(move);
                int value = Search(board, depth - 1, MIN, MAX, true);
                board.undoMove();
                if (value > best_value){
                    best_value = value;
                    best_move = move;
                }
            }
            return best_move;
        } else if ((AIPlayer == Side.BLACK) && board.getSideToMove() == Side.BLACK){
            Move worst_move = null;
            int worst_value = MAX;
            for (Move move : board.legalMoves()){
                board.doMove(move);
                int value = Search(board, depth - 1, MIN, MAX, true);
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


    public int quiesce(Board board, int alpha, int beta, int depth){
        nodes++;
        if (depth <= 0){
            return this.eval.evaluate(board);
        }
        int eval = this.eval.evaluate(board);
        if (eval >= beta){
            return beta;
        }
        if (alpha < eval){
            alpha = eval;
        }
        List<Move> moves = sort.MVVLVA(board.legalMoves(), board);

        for (Move move : moves){
            if (board.getPiece(move.getTo()) != Piece.NONE){
                board.doMove(move);
                int score = -quiesce(board, alpha, beta, depth-1);
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
    }

    private boolean canApplyNullMove(Board board, int depth){
        return board.getMoveCounter() >= (R + 1) && !isEndgame(board); // changed from depth >= R + 1
    }

    public int Search(Board board, int depth, int alpha, int beta, boolean allowNull) {
        nodes++;

        if (depth <= 0 || board.isDraw() || board.isMated()){
            return eval.evaluate(board);//quiesce(board, alpha, beta, 5);
        }
        //System.out.println(board.getSideToMove() == AIPlayer && allowNull && !board.isKingAttacked());
        if (board.getSideToMove() == AIPlayer /*&& canApplyNullMove(board, depth) */&& allowNull && !board.isKingAttacked()){
            board.doNullMove();
            int value = -Search(board, depth - R - 1, -beta, -beta + 1, false);
            board.undoMove();
            if (AIPlayer == Side.WHITE){
                if (value <= alpha){
                    return value;
                }
            } else {
                if (value >= beta){
                    return value;
                }
            }
        }

        List<Move> legalMoves = sort.MVVLVA(board.legalMoves(), board);

        long hash = board.getZobristKey();
        if (tt.containsKey(hash)){
            TTentry ent = tt.get(hash);
            if (ent.getDepth() >= depth){
                return ent.getBestValue();
            } else if (ent.getPV() != null) {
                legalMoves.add(0, ent.getPV());
            }
        }

        if (board.getSideToMove() == Side.WHITE){
            Move best_move = null;
            int best_value = MIN;
            int value;

            for (Move move : legalMoves){
                board.doMove(move);
                value = Search(board, depth -1, alpha, beta, true); //dynamic memory allocation
                if (value > best_value){
                    best_value = value;
                    best_move = move;
                }
                board.undoMove();
                alpha = Math.max(alpha, best_value);
                if (beta <= alpha){
                    break;
                }

            }
            TTentry entry = new TTentry(best_move, best_value, depth);
            tt.put(board.getZobristKey(), entry);
            return best_value;
        } else {
            Move worst_move = null;
            int worst_value = MAX;
            int value;

            for (Move move : legalMoves){
                board.doMove(move);
                value = Search(board, depth -1, alpha, beta, true);
                if (value < worst_value){
                    worst_value = value;
                    worst_move = move;
                }
                board.undoMove();
                beta = Math.min(beta, value);
                if (beta <= alpha){
                    break;
                }
            }
            TTentry entry = new TTentry(worst_move, worst_value, depth);
            tt.put(board.getZobristKey(), entry);
            return worst_value;
        }
    }

    public int MTDf(Board root, int f, int d){
        int g = f;
        int upperbound = MAX;
        int lowerbound = MIN;
        int beta;
        while (lowerbound < upperbound){
            if (g == lowerbound){
                beta = g + 1;
            } else {
                beta = g;
            }
            g = Search(root, d,beta - 1, beta, true);
            if (g < beta){
                upperbound = g;
            } else {
                lowerbound = g;
            }
        }
    return g;
    }


}

/*

### WITH NULL MOVE R = 3
    Enter a side to play as (white/black):
white
rnbqkbnr
pppppppp
........
........
........
........
PPPPPPPP
RNBQKBNR
Side: WHITE
Enter move:
e4
AI Played: b8c6
Calculation time: 4098 Milliseconds
r.bqkbnr
pppppppp
..n.....
........
....P...
........
PPPP.PPP
RNBQKBNR
Side: WHITE
static eval: -10
Legal moves: [a2a3, a2a4, b2b3, b2b4, c2c3, c2c4, d2d3, d2d4, f2f3, f2f4, g2g3, g2g4, h2h3, h2h4, e4e5, b1a3, b1c3, g1e2, g1f3, g1h3, f1e2, f1d3, f1c4, f1b5, f1a6, d1e2, d1f3, d1g4, d1h5, e1e2]
Enter move:
d4
AI Played: e7e6
Calculation time: 5937 Milliseconds
r.bqkbnr
pppp.ppp
..n.p...
........
...PP...
........
PPP..PPP
RNBQKBNR
Side: WHITE
static eval: 10
Legal moves: [a2a3, a2a4, b2b3, b2b4, c2c3, c2c4, f2f3, f2f4, g2g3, g2g4, h2h3, h2h4, d4d5, e4e5, b1d2, b1a3, b1c3, g1e2, g1f3, g1h3, c1d2, c1e3, c1f4, c1g5, c1h6, f1e2, f1d3, f1c4, f1b5, f1a6, d1d2, d1e2, d1d3, d1f3, d1g4, d1h5, e1d2, e1e2]
Enter move:
Nf3
AI Played: d8f6
Calculation time: 14020 Milliseconds
r.b.kbnr
pppp.ppp
..n.pq..
........
...PP...
.....N..
PPP..PPP
RNBQKB.R
Side: WHITE
static eval: 50
Legal moves: [a2a3, a2a4, b2b3, b2b4, c2c3, c2c4, g2g3, g2g4, h2h3, h2h4, d4d5, e4e5, b1d2, b1a3, b1c3, f3g1, f3d2, f3h4, f3e5, f3g5, c1d2, c1e3, c1f4, c1g5, c1h6, f1e2, f1d3, f1c4, f1b5, f1a6, h1g1, d1d2, d1e2, d1d3, e1d2, e1e2]
Enter move:
e5
AI Played: f6f5
Calculation time: 7471 Milliseconds
r.b.kbnr
pppp.ppp
..n.p...
....Pq..
...P....
.....N..
PPP..PPP
RNBQKB.R
Side: WHITE
static eval: 55
Legal moves: [a2a3, a2a4, b2b3, b2b4, c2c3, c2c4, g2g3, g2g4, h2h3, h2h4, d4d5, b1d2, b1a3, b1c3, f3g1, f3d2, f3h4, f3g5, c1d2, c1e3, c1f4, c1g5, c1h6, f1e2, f1d3, f1c4, f1b5, f1a6, h1g1, d1d2, d1e2, d1d3, e1d2, e1e2]
Enter move:
Bd3
AI Played: f5g4
Calculation time: 33713 Milliseconds
r.b.kbnr
pppp.ppp
..n.p...
....P...
...P..q.
...B.N..
PPP..PPP
RNBQK..R
Side: WHITE
static eval: 80
Legal moves: [d3h7, a2a3, a2a4, b2b3, b2b4, c2c3, c2c4, g2g3, h2h3, h2h4, d4d5, b1d2, b1a3, b1c3, f3g1, f3d2, f3h4, f3g5, c1d2, c1e3, c1f4, c1g5, c1h6, d3f1, d3e2, d3c4, d3e4, d3b5, d3f5, d3a6, d3g6, h1f1, h1g1, d1d2, d1e2, e1f1, e1d2, e1e2, e1g1]
Enter move:
h3
AI Played: g4g2
Calculation time: 10409 Milliseconds
r.b.kbnr
pppp.ppp
..n.p...
....P...
...P....
...B.N.P
PPP..Pq.
RNBQK..R
Side: WHITE
static eval: -30
Legal moves: [d3h7, a2a3, a2a4, b2b3, b2b4, c2c3, c2c4, h3h4, d4d5, b1d2, b1a3, b1c3, f3g1, f3d2, f3h2, f3h4, f3g5, c1d2, c1e3, c1f4, c1g5, c1h6, d3f1, d3e2, d3c4, d3e4, d3b5, d3f5, d3a6, d3g6, h1f1, h1g1, h1h2, d1d2, d1e2, e1d2, e1e2]
Enter move:
Rh2
AI Played: f8b4
Calculation time: 22280 Milliseconds
r.b.k.nr
pppp.ppp
..n.p...
....P...
.b.P....
...B.N.P
PPP..PqR
RNBQK...
Side: WHITE
static eval: -50
Legal moves: [c2c3, b1d2, b1c3, f3d2, c1d2, d1d2, e1e2]
Enter move:
c3
AI Played: g2h2
Calculation time: 11451 Milliseconds
r.b.k.nr
pppp.ppp
..n.p...
....P...
.b.P....
..PB.N.P
PP...P.q
RNBQK...
Side: WHITE
static eval: -555
Legal moves: [f3h2, c3b4, d3h7, a2a3, a2a4, b2b3, h3h4, d4d5, b1d2, b1a3, f3g1, f3d2, f3h4, f3g5, c1d2, c1e3, c1f4, c1g5, c1h6, d3f1, d3c2, d3e2, d3c4, d3e4, d3b5, d3f5, d3a6, d3g6, d1c2, d1d2, d1e2, d1b3, d1a4, e1f1, e1d2, e1e2]
Enter move:

Process finished with exit code 130

*/

