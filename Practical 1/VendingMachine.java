import java.util.Scanner;

public class VendingMachine 
{
    public static void main(String[] args)
    {
        enum Coin {ONE ,TWO, FIVE, TEN}
        
        int SnackPrice=15;
        int total=0;
        
        System.out.println("Please insert coins (ONE/TWO/FIVE/TEN):");

        while(total<SnackPrice)
        {
            Scanner input = new Scanner(System.in);
            Coin coin = Coin.valueOf(input.nextLine().toUpperCase());

            switch(coin)
            {
                case ONE:
                    total+=1;
                    break;
                case TWO:
                    total+=2;
                    break;
                case FIVE:
                    total+=5;
                    break;
                case TEN:
                    total+=10;
                    break;
                default:
                    System.out.println("Invalid coin");
                    break;
            }
            System.out.println("Total inserted: " + total);
        }
        if(total>=SnackPrice)
        {
            System.out.println("paid successfully");
            int change=total-SnackPrice;
            if(change>0)
            {
                System.out.println("Change returned: " + change);
            }
        }
    }
}