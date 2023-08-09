package CatFish;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        Eval eval = new Eval();
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
            System.out.println(board.toString() + "\n");
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

            AImove = CatFish.pickMove(board,5); // Should never return null.
            board.doMove(AImove);
            System.out.println("AI Played: " + AImove);
            System.out.println(board.toString() + "\n");
            System.out.println("static eval: " + eval.evaluate(board));
            if (board.isMated() || board.isDraw()){
                System.out.println("Game Over!");
                break;
            }

        }
    }
}
