import java.util.Objects;

public class Card 
{
    private String rank;
    private String suit;

    public Card(String rank, String suit) 
    {
        this.rank = rank;
        this.suit = suit;
    }

    @Override
    public String toString() 
    {
        return rank + " of " + suit;
    }

    @Override
    public boolean equals(Object obj) 
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Card other = (Card) obj;
        return this.rank.equals(other.rank) && this.suit.equals(other.suit);
    }

    @Override
    public int hashCode() 
    {
        return Objects.hash(rank, suit);
    }
}
