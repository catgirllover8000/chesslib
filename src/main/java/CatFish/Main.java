package CatFish;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.unicode.UnicodePrinter;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        Eval eval = new Eval();
        Sort sort = new Sort();
        Engine CatFish = null;
        String move;
        Move AImove;


        System.out.println("Enter a side to play as (white/black): ");
        Scanner sc = new Scanner(System.in);
        String playerSide = sc.nextLine();
        if (playerSide.equals("white")){

            CatFish = new Engine(Side.BLACK);
            System.out.println(board.toString());

        } else if (playerSide.equals("black")) {
            CatFish = new Engine(Side.WHITE);
            AImove = CatFish.pickMove(board,5);
            board.doMove(AImove);
            System.out.println("AI Played: " + AImove);
            System.out.println(board.toString());
        } else {
            System.out.println("bad");
        }
        while (!board.isMated() && !board.isDraw()){
            System.out.println("Enter move: ");
            sc = new Scanner(System.in);
            move = sc.nextLine();
            board.doMove(move);

            if (board.isMated() || board.isDraw()){
                System.out.println("Game Over!");
                break;
            }

            long startTime = System.currentTimeMillis();
            AImove = CatFish.pickMove(board,5); // Should never return null.
            //System.out.println("\n" + CatFish.MTDf(board, 0, 5)); // newline
            //System.out.println(CatFish.Search(board, 5, Engine.MIN, Engine.MAX, true));
            long endTime = System.currentTimeMillis();
            board.doMove(AImove);
            System.out.println("AI Played: " + AImove);
            System.out.println("Calculation time: " + (endTime - startTime) + " Milliseconds");
            System.out.println(board.toString());
            System.out.println("Nodes: " + Engine.nodes);
            //System.out.println("Legal moves: " + sort.MVVLVA(board.legalMoves(), board));
            System.out.println("\n");
            if (board.isMated() || board.isDraw()){
                System.out.println("Game Over!");
                break;
            }

        }
    }
}
