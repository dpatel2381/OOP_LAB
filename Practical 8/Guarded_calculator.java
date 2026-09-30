import java.util.InputMismatchException;
import java.util.Scanner;

class DivideByZeroException extends Exception 
{
    public DivideByZeroException(String message) 
    {
        super(message);
    }
}

public class Guarded_calculator 
{

    public static double calculate(double num1, double num2, char op) throws DivideByZeroException 
    {
        switch (op) 
        {
            case '+':
                return num1 + num2;

            case '-':
                return num1 - num2;

            case '*':
                return num1 * num2;

            case '/':
                if (num2 == 0) 
                {
                    throw new DivideByZeroException("Error: Division by zero is not allowed.");
                }
                return num1 / num2;

            default:
                throw new IllegalArgumentException("Invalid operator! Use +, -, * or /");
        }
    }

    public static void main(String[] args) 
    {
        Scanner sc = new Scanner(System.in);
        boolean success = false;

        while (!success) 
        {
            try 
            {
                System.out.print("Enter first number: ");
                double num1 = sc.nextDouble();

                System.out.print("Enter operator (+, -, *, /): ");
                char op = sc.next().charAt(0);

                System.out.print("Enter second number: ");
                double num2 = sc.nextDouble();

                double result = calculate(num1, num2, op);

                System.out.println("Result = " + result);
                success = true; // valid calculation

            } 
            catch (InputMismatchException e) 
            {
                System.out.println("Invalid number input! Please enter numeric values.");
                sc.nextLine(); // clear invalid input

            } 
            catch (DivideByZeroException e) 
            {
                System.out.println(e.getMessage());

            } 
            catch (IllegalArgumentException e) 
            {
                System.out.println(e.getMessage());

            } 
            finally 
            {
                System.out.println("Calculation attempt logged.\n");
            }
        }

        sc.close();
    }
}