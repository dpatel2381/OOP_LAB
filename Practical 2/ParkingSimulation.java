class ParkingLot 
{
    private int twoWheelers;
    private int fourWheelers;
    private final int twoCap;
    private final int fourCap;
    private static long revenue = 0;

    public ParkingLot(int twoCap, int fourCap) 
    {
        this.twoCap = twoCap;
        this.fourCap = fourCap;
        this.twoWheelers = 0;
        this.fourWheelers = 0;
    }

    public void park(String type) 
    {
        switch (type.toLowerCase()) 
        {
            case "two":
                if (twoWheelers < twoCap) 
                {
                    twoWheelers++;
                    revenue += 20;
                    System.out.println("Two-wheeler parked. Revenue +20");
                } else {
                    System.out.println("Full for two-wheelers. Parking rejected.");
                }
                break;

            case "four":
                if (fourWheelers < fourCap) 
                {
                    fourWheelers++;
                    revenue += 40;
                    System.out.println("Four-wheeler parked. Revenue +40");
                } 
                else 
                {
                    System.out.println("Full for four-wheelers. Parking rejected.");
                }
                break;

            default:
                System.out.println("Invalid vehicle type.");
        }
    }

    public void leave(String type) 
    {
        switch (type.toLowerCase()) 
        {
            case "two":
                if (twoWheelers > 0) 
                {
                    twoWheelers--;
                    System.out.println("Two-wheeler left.");
                } 
                else 
                {
                    System.out.println("No two-wheelers to leave.");
                }
                break;

            case "four":
                if (fourWheelers > 0) 
                {
                    fourWheelers--;
                    System.out.println("Four-wheeler left.");
                } 
                else 
                {
                    System.out.println("No four-wheelers to leave.");
                }
                break;

            default:
                System.out.println("Invalid vehicle type.");
        }
    }

    public void printStatus() 
    {
        System.out.println("\n--- Final Parking Lot Status ---");
        System.out.println("Two-wheelers: " + twoWheelers + "/" + twoCap);
        System.out.println("Four-wheelers: " + fourWheelers + "/" + fourCap);
        System.out.println("Total Revenue: " + revenue);
    }
}

public class ParkingSimulation
{
    public static void main(String[] args) 
    {
        ParkingLot lot = new ParkingLot(2, 2); // capacity: 2 two-wheelers, 2 four-wheelers

        lot.park("two");
        lot.park("two");
        lot.park("two");
        lot.park("four");
        lot.park("four");
        lot.park("four");

        lot.leave("two");
        lot.leave("four");
        lot.leave("four");

        lot.printStatus();
    }
}
