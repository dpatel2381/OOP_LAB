import java.util.*;

public class unique {
    public static void main(String[] args) {
        List<String> logins = Arrays.asList(
            "Asha", "Rahul", "Asha", "Meera",
            "Rahul", "Dev", "Meera"
        );

        Set<String> uniqueUsers = new HashSet<>(logins);
        Set<String> orderedUsers = new LinkedHashSet<>(logins);

        System.out.println("Unique visitors: " + uniqueUsers.size());
        System.out.println("First three distinct visitors:");

        int count = 0;
        for (String user : orderedUsers) {
            if (count == 3) {
                break;
            }
            System.out.println(user);
            count++;
        }
    }
}