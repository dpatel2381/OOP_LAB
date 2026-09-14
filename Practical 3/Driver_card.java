public class Driver_card 
{
    public static void main(String[] args) 
    {
        Card[] cards = new Card[6];

        cards[0] = new Card("Queen", "Hearts");
        cards[1] = new Card("Ace", "Spades");
        cards[2] = new Card("King", "Diamonds");
        cards[3] = new Card("Queen", "Hearts"); // duplicate
        cards[4] = new Card("Jack", "Clubs");
        cards[5] = new Card("Ace", "Spades");   // duplicate

        for (int i = 0; i < cards.length; i++) 
        {
            for (int j = 0; j < i; j++) 
            {
                if (cards[i].equals(cards[j])) 
                {
                    System.out.println("Duplicate found: " + cards[i]);
                   
                    return;
                }
            }
        }

        System.out.println("No duplicates found.");
    }
}

