import java.util.Scanner;

record BankInfo(String name, String branch) {}

enum MenuOption 
{
    OPEN_ACCOUNT,
    DEPOSIT,
    WITHDRAW,
    TRANSFER,
    EXIT
}

public class MiniBank 
{
    public static void main(String[] args) 
    {
        BankInfo bank = new BankInfo("MiniBank", "Main Branch");
        System.out.println("==================================");
        System.out.println(" Welcome to " + bank.name() + " - " + bank.branch());
        System.out.println("==================================");

        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) 
        {
            System.out.println("\nPlease choose an option:");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Exit");

            System.out.print("Enter choice (1-5): ");
            int choice = sc.nextInt();

            MenuOption option = switch (choice) 
            {
                case 1 -> MenuOption.OPEN_ACCOUNT;
                case 2 -> MenuOption.DEPOSIT;
                case 3 -> MenuOption.WITHDRAW;
                case 4 -> MenuOption.TRANSFER;
                case 5 -> MenuOption.EXIT;
                default -> 
                {
                    System.out.println("Invalid choice. Try again.");
                    yield null;
                }
            };

            if (option != null) 
            {
                switch (option) 
                {
                    case OPEN_ACCOUNT -> System.out.println("Open Account — to be implemented in a later lab.");
                    case DEPOSIT -> System.out.println("Deposit — to be implemented in a later lab.");
                    case WITHDRAW -> System.out.println("Withdraw — to be implemented in a later lab.");
                    case TRANSFER -> System.out.println("Transfer — to be implemented in a later lab.");
                    case EXIT -> 
                    {
                        System.out.println("Thank you for using MiniBank. Goodbye!");
                        running = false;
                    }
                }
            }
        }

        Account[] accounts = new Account[3];
        accounts[0] = new Account("Alice", 1000);
        accounts[1] = new Account("Bob");
        accounts[2] = new Account("Charlie", 500);

        accounts[0].deposit(500); 
        accounts[1].deposit(200);
        accounts[2].withdraw(100);

        for (Account acc : accounts) 
            {
            System.out.println(acc.getOwnerName() + " (" + acc.getAccountNumber() + ") Balance: " + acc.getBalance());
        }

        sc.close();
    }
}
