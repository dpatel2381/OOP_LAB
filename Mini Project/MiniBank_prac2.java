public class MiniBank_prac2 {

    // ---------------- Customer Class ----------------
    static class Customer {
        private String name;
        private String email;
        private String mobile;
        private final String customerId;

        private static long customerCounter = 100; // start from 100

        // Constructor
        public Customer(String name, String email, String mobile) {
            this.name = name;
            this.email = email;
            this.mobile = mobile;
            this.customerId = generateCustomerId();
        }

        // Generate unique customer ID
        private static String generateCustomerId() {
            customerCounter++;
            return "CUST" + customerCounter;
        }

        // Getters
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getMobile() { return mobile; }
        public String getCustomerId() { return customerId; }
    }

    // ---------------- Account Class ----------------
    static class Account {
        private final String accountNumber;
        private String ownerName;
        private long balance;
        private boolean active;

        private static int accountCounter = 1;

        // Constructor with opening balance
        public Account(String ownerName, long openingBalance) {
            this.ownerName = ownerName;
            this.balance = openingBalance;
            this.active = true;
            this.accountNumber = generateAccountNumber();
        }

        // Constructor with zero balance
        public Account(String ownerName) {
            this(ownerName, 0);
        }

        // Generate unique account number
        private static String generateAccountNumber() {
            return String.format("AC%04d", accountCounter++);
        }

        // Deposit method
        public void deposit(long amount) {
            if (amount > 0) {
                balance += amount;
            }
        }

        // Withdraw method
        public boolean withdraw(long amount) {
            if (amount > 0 && balance >= amount) {
                balance -= amount;
                return true;
            }
            return false;
        }

        // Getters
        public String getAccountNumber() { return accountNumber; }
        public String getOwnerName() { return ownerName; }
        public long getBalance() { return balance; }
        public boolean isActive() { return active; }
    }

    // ---------------- Main Method ----------------
    public static void main(String[] args) {
        // Create three accounts
        Account[] accounts = new Account[3];
        accounts[0] = new Account("Alice", 1000);
        accounts[1] = new Account("Bob");
        accounts[2] = new Account("Charlie", 500);

        // Perform deposits and withdrawals
        accounts[0].deposit(500);   // Alice deposits 500
        accounts[1].deposit(200);   // Bob deposits 200
        accounts[2].withdraw(100);  // Charlie withdraws 100

        // Print balances
        for (Account acc : accounts) {
            System.out.println(acc.getOwnerName() + " (" + acc.getAccountNumber() +
                               ") Balance: " + acc.getBalance());
        }
    }
}
