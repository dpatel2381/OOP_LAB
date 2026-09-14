import java.util.Scanner;

public class Driver_chat 
{
    public static void main(String[] args) 
    {
        String[] logs = 
        {
            "10:05 alice Hello there",
            "10:06 bob How are you?",
            "10:07 alice I am fine",
            "10:08 charlie Good morning everyone",
            "10:09 malformedline" 
        };

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter keyword to search: ");
        String keyword = sc.nextLine();

        ChatFilter filter = new ChatFilter(keyword);

        for (String log : logs) 
        {
            filter.processLine(log);
        }

        filter.printReport();

        sc.close();
    }
}

