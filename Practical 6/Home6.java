import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

// Functional Interface
@FunctionalInterface
interface DiscountRule {
    double apply(double price);
}

public class Home6 {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // List of prices
        List<Double> prices = Arrays.asList(
                100.0,
                250.0,
                500.0,
                1000.0
        );

        System.out.println("=== Discount Engine ===");
        System.out.println("1. 10% Discount");
        System.out.println("2. 20% Discount");
        System.out.println("3. Flat Rs.50 Discount");
        System.out.print("Choose a rule: ");

        int choice = sc.nextInt();

        DiscountRule rule;

        switch (choice) {
            case 1:
                rule = price -> price * 0.90;
                break;

            case 2:
                rule = price -> price * 0.80;
                break;

            case 3:
                rule = price -> price - 50;
                break;

            default:
                System.out.println("Invalid Choice!");
                sc.close();
                return;
        }

        System.out.println("\nOriginal Price -> Final Price");

        for (double price : prices) {
            double discountedPrice = rule.apply(price);
            System.out.println(price + " -> " + discountedPrice);
        }

        sc.close();
    }
}