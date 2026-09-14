import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordChecker 
{
    public static void main(String[] args) 
    {
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}.+$");

        Scanner sc= new Scanner(System.in);
        System.out.println("Enter the password to check: ");
        String password = sc.nextLine();

        Matcher matcher= pattern.matcher(password);

        int ruleCount = 0;

            if(pattern.compile(".{8,}.").matcher(password).matches())
            {
               ruleCount++;
            }
            if(pattern.compile(".*[A-Z].*").matcher(password).matches())
            {
               ruleCount++;
            }
            if(pattern.compile(".*[0-9].*").matcher(password).matches())
            {
                ruleCount++;
            }
            if(pattern.compile(".*[@#$%^&+=].*").matcher(password).matches())
            {
                ruleCount++;
            }
 

            if(ruleCount == 4)
            {
                System.out.println(password + " -> strong");
            }
            else if(ruleCount==2 || ruleCount==3)
            {
                System.out.println(password + " -> moderate");
            }
            else
            {
                System.out.println(password + " -> weak");
            }
    } 
}