import java.util.Scanner;

record Vehicle(String number, String type) {}

public class TollBooth
{
    public static void main(String[] args) 
    {
        Scanner sc = new Scanner(System.in);

        int totalToll = 0;
        int bikeCount = 0;
        int carCount = 0;
        int truckCount = 0;

        while (true) 
        {
            System.out.print("Enter vehicle number (or 'done' to finish): ");
            String number = sc.nextLine().trim();

            if (number.equalsIgnoreCase("done")) 
            {
                break;
            }

            System.out.print("Enter vehicle type (bike/car/truck): ");
            String type = sc.nextLine().trim().toLowerCase();

            Vehicle v = new Vehicle(number, type);

            int toll = switch (v.type()) 
            {
                case "bike" -> 20;
                case "car" -> 50;
                case "truck" -> 150;
                default -> 
                {
                    System.out.println("Unknown vehicle type. Toll = 0");
                    yield 0;
                }
            };

            totalToll += toll;

            switch (v.type()) 
            {
                case "bike" -> bikeCount++;
                case "car" -> carCount++;
                case "truck" -> truckCount++;
            }
        }

        System.out.println("\n--- Toll Summary ---");
        System.out.println("Total Toll Collected: " + totalToll);
        System.out.println("Bike Count: " + bikeCount);
        System.out.println("Car Count: " + carCount);
        System.out.println("Truck Count: " + truckCount);

        String highestType;
        int maxCount = Math.max(bikeCount, Math.max(carCount, truckCount));
        if (maxCount == bikeCount) 
        {
            highestType = "bike";
        } 
        else if (maxCount == carCount) 
        {
            highestType = "car";
        } 
        else 
        {
            highestType = "truck";
        }

        System.out.println("Vehicle type with highest count: " + highestType);
        sc.close();
    }
}
