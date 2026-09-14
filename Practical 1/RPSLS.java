import java.util.*;

enum Move 
{
    ROCK, PAPER, SCISSORS, LIZARD, SPOCK;
}

public class RPSLS 
{

    public static int winner(Move a, Move b) 
    {
        if (a == b) return 0;

        return switch (a) 
        {
            case SCISSORS -> (b == Move.PAPER || b == Move.LIZARD) ? 1 : -1;
            case PAPER    -> (b == Move.ROCK || b == Move.SPOCK) ? 1 : -1;
            case ROCK     -> (b == Move.SCISSORS || b == Move.LIZARD) ? 1 : -1;
            case LIZARD   -> (b == Move.SPOCK || b == Move.PAPER) ? 1 : -1;
            case SPOCK    -> (b == Move.SCISSORS || b == Move.ROCK) ? 1 : -1;
        };
    }

    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        Random rand = new Random();

        int playerScore = 0;
        int computerScore = 0;

        System.out.println("Welcome to Rock-Paper-Scissors-Lizard-Spock!");
        System.out.println("Valid moves: ROCK, PAPER, SCISSORS, LIZARD, SPOCK\n");

        for (int round = 1; round <= 5; round++) 
        {
            System.out.print("Round " + round + " - Enter your move: ");
            String input = sc.nextLine().trim().toUpperCase();

            Move playerMove;
            try 
            {
                playerMove = Move.valueOf(input);
            } 
            catch (IllegalArgumentException e) 
            {
                System.out.println("Invalid move! Try again.");
                round--;
                continue;
            }

            Move computerMove = Move.values()[rand.nextInt(Move.values().length)];
            System.out.println("Computer chose: " + computerMove);

            int result = winner(playerMove, computerMove);

            if (result == 1) 
            {
                System.out.println("You win this round!");
                playerScore++;
            } 
            else if (result == -1) 
            {
                System.out.println("Computer wins this round!");
                computerScore++;
            } 
            else 
            {
                System.out.println("This round is a tie!");
            }
            System.out.println("Score: You " + playerScore + " - Computer " + computerScore + "\n");
        }

        System.out.println("--- Final Result ---");
        if (playerScore > computerScore) 
        {
            System.out.println("You are the overall winner!");
        } 
        else if (computerScore > playerScore) 
        {
            System.out.println("Computer is the overall winner!");
        } else 
        {
            System.out.println("It's an overall tie!");
        }

        sc.close();
    }
}
