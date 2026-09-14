public class MiniBank_prac3 {

    // ---------------- Customer Class ----------------
    static class Customer implements Cloneable {
        private String name;
        private String email;
        private String mobile;
        private final String customerId;

        private static long customerCounter = 100; // start from 100

        private Address address; // Address field

        // Public static nested Address class
        public static class Address {
            private String line;
            private String city;
            private String pincode;

            public Address(String line, String city, String pincode) {
                this.line = line;
                this.city = city;
                this.pincode = pincode;
            }

            public String getLine() { return line; }
            public String getCity() { return city; }
            public String getPincode() { return pincode; }

            @Override
            public String toString() {
                return line + ", " + city + " - " + pincode;
            }
        }

        // Constructor
        public Customer(String name, String email, String mobile, Address address) {
            this.name = name;
            this.email = email;
            this.mobile = mobile;
            this.customerId = generateCustomerId();
            this.address = address;
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
        public Address getAddress() { return address; }

        // Clone method
        @Override
        public Customer clone() {
            try {
                return (Customer) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException("Clone not supported", e);
            }
        }

        @Override
        public String toString() {
            return customerId + " | " + name + " | " + email + " | " + mobile + " | " + address;
        }
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

        // Override toString
        @Override
        public String toString() {
            return accountNumber + " | " + ownerName + " | Balance: ₹" + balance;
        }

        // Override equals
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Account)) return false;
            Account account = (Account) o;
            return accountNumber.equals(account.accountNumber);
        }

        // Override hashCode
        @Override
        public int hashCode() {
            return accountNumber.hashCode();
        }
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

        // Print accounts using toString()
        System.out.println("=== Account Details ===");
        for (Account acc : accounts) {
            System.out.println(acc);
        }

        // Compare two Account objects with equals()
        System.out.println("\nAccount equality check: " + accounts[0].equals(accounts[1]));

        // instanceof check
        if (accounts[0] instanceof Account) {
            System.out.println("accounts[0] is an Account object.");
        }

        // Create a Customer with Address
        Customer.Address addr = new Customer.Address("123 Main St", "Sojitra", "388460");
        Customer cust1 = new Customer("Dhanvi", "dhanvi@example.com", "9876543210", addr);

        // Clone the Customer
        Customer cust2 = cust1.clone();

        // Print original and cloned Customer
        System.out.println("\n=== Customer Details ===");
        System.out.println("Original: " + cust1);
        System.out.println("Cloned:   " + cust2);
    }
}
