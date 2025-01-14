package CatFish;

import com.github.bhlangonijr.chesslib.*;

import java.util.HashMap;

public class Eval {

    HashMap<PieceType,Integer> pieceToIndex = new HashMap<>();

    public Eval() {
        pieceToIndex.put(PieceType.PAWN, 0);
        pieceToIndex.put(PieceType.KNIGHT, 1);
        pieceToIndex.put(PieceType.BISHOP, 2);
        pieceToIndex.put(PieceType.ROOK, 3);
        pieceToIndex.put(PieceType.QUEEN, 4);
        pieceToIndex.put(PieceType.KING, 5);

    }

    private final int[] squareIndices =
            {56, 57, 58, 59, 60, 61, 62, 63,
            48, 49, 50, 51, 52, 53, 54, 55,
            40, 41, 42, 43, 44, 45, 46, 47,
            32, 33, 34, 35, 36, 37, 38, 39,
            24, 25, 26, 27, 28, 29, 30, 31,
            16, 17, 18, 19, 20, 21, 22, 23,
            8, 9, 10, 11, 12, 13, 14, 15,
            0, 1, 2, 3, 4, 5, 6, 7};

    private final int[][] pst = {
            //Pawn
            {0,  0,  0,  0,  0,  0,  0,  0,
            50, 50, 50, 50, 50, 50, 50, 50,
            10, 10, 20, 30, 30, 20, 10, 10,
            5,  5, 10, 25, 25, 10,  5,  5,
            0,  0,  0, 20, 20,  0,  0,  0,
            5, -5,-10,  0,  0,-10, -5,  5,
            5, 10, 10,-20,-20, 10, 10,  5,
            0,  0,  0,  0,  0,  0,  0,  0},
            //Knight
            {-50,-40,-30,-30,-30,-30,-40,-50,
            -40,-20,  0,  0,  0,  0,-20,-40,
            -30,  0, 10, 15, 15, 10,  0,-30,
            -30,  5, 15, 20, 20, 15,  5,-30,
            -30,  0, 15, 20, 20, 15,  0,-30,
            -30,  5, 10, 15, 15, 10,  5,-30,
            -40,-20,  0,  5,  5,  0,-20,-40,
            -50,-40,-30,-30,-30,-30,-40,-50},
            //Bishop
            {-20,-10,-10,-10,-10,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5, 10, 10,  5,  0,-10,
            -10,  5,  5, 10, 10,  5,  5,-10,
            -10,  0, 10, 10, 10, 10,  0,-10,
            -10, 10, 10, 10, 10, 10, 10,-10,
            -10,  5,  0,  0,  0,  0,  5,-10,
            -20,-10,-10,-10,-10,-10,-10,-20},
            //Rook
            { 0,  0,  0,  0,  0,  0,  0,  0,
            5, 10, 10, 10, 10, 10, 10,  5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            0,  0,  0,  5,  5,  0,  0,  0},
            //Queen
            {-20,-10,-10, -5, -5,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5,  5,  5,  5,  0,-10,
            -5,  0,  5,  5,  5,  5,  0, -5,
            0,  0,  5,  5,  5,  5,  0, -5,
            -10,  5,  5,  5,  5,  5,  0,-10,
            -10,  0,  5,  0,  0,  0,  0,-10,
            -20,-10,-10, -5, -5,-10,-10,-20},
            //King middlegame
            {-30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -20,-30,-30,-40,-40,-30,-30,-20,
            -10,-20,-20,-20,-20,-20,-20,-10,
            20, 20,  0,  0,  0,  0, 20, 20,
            20, 30, 10,  0,  0, 10, 30, 20},
            //King endgame
            {-50,-40,-30,-20,-20,-30,-40,-50,
            -30,-20,-10,  0,  0,-10,-20,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-30,  0,  0,  0,  0,-30,-30,
            -50,-30,-30,-30,-30,-30,-30,-50}
    };
    Board board;

    public static boolean isEndgame(Board board){
        int whitequeens = 0;
        int blackqueens = 0;
        int whiteminor = 0;
        int blackminor = 0;
        for(Square square : Square.values()){
            Piece piece = board.getPiece(square);
            if (piece.getPieceType() == PieceType.QUEEN){
                if (piece.getPieceSide() == Side.WHITE){
                    whitequeens++;
                } else {
                    blackqueens++;
                }
            } else if (piece.getPieceType() != PieceType.PAWN && piece.getPieceType()!= PieceType.KING) {
                if (piece.getPieceSide() == Side.WHITE){
                    whiteminor++;
                } else {
                    blackminor++;
                }
            }
        }
        boolean white_endgame = (whitequeens == 0) || (whitequeens == 1 && whiteminor <= 1);
        boolean black_endgame = (blackqueens == 0) || (blackqueens == 1 && blackminor <= 1);
        return white_endgame && black_endgame;
    }

    public int evaluate(Board board){
        this.board = board;
        if (board.isDraw()){
            return 0;
        } else if (board.isMated()) {
            if (board.getSideToMove() == Side.WHITE){
                return -10000;
            } else {
                return 10000;
            }


        }
        int eval = 0;
        for (int n = 0; n < Square.values().length;n++){
            Square square = Square.values()[n];
            Piece piece = board.getPiece(square);
            PieceType pt = piece.getPieceType();

            if (piece != Piece.NONE){
                int pieceValue = 0;
                switch (piece.getPieceType()){
                    case PAWN:
                        pieceValue = 100;
                        break;
                    case KNIGHT:
                        pieceValue = 320;
                        break;
                    case BISHOP:
                        pieceValue = 330;
                        break;
                    case ROOK:
                        pieceValue = 500;
                        break;
                    case QUEEN:
                        pieceValue = 900;
                        break;
                    case KING:
                        pieceValue = 10000;
                        break;
                }
                if (pt == PieceType.KING && isEndgame(board)){
                    if (piece.getPieceSide() == Side.WHITE){
                        eval += pieceValue + pst[6][squareIndices[square.ordinal()]];
                    } else {
                        eval -= pieceValue + pst[6][63 - squareIndices[square.ordinal()]];
                    }
                } else {
                    if (piece.getPieceSide() == Side.WHITE){
                        eval += pieceValue + pst[pieceToIndex.get(pt)][squareIndices[square.ordinal()]];
                    } else {
                        eval -= pieceValue + pst[pieceToIndex.get(pt)][63 - squareIndices[square.ordinal()]];
                    }

                }
            }
        }
        return eval;
    }
}
