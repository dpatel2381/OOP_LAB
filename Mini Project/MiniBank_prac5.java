import java.util.regex.Pattern;

public class MiniBank_prac5 {

    static class Validator {
        private static final Pattern MOBILE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
        private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
        private static final Pattern PAN_PATTERN = Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]{1}$");
        private static final Pattern IFSC_PATTERN = Pattern.compile("^[A-Z]{4}0[0-9]{6}$");

        public static boolean isValidMobile(String mobile) {
            return MOBILE_PATTERN.matcher(mobile).matches();
        }

        public static boolean isValidEmail(String email) {
            return EMAIL_PATTERN.matcher(email).matches();
        }

        public static boolean isValidPan(String pan) {
            return PAN_PATTERN.matcher(pan).matches();
        }

        public static boolean isValidIfsc(String ifsc) {
            return IFSC_PATTERN.matcher(ifsc).matches();
        }
    }

    enum TransactionType {
        DEPOSIT, WITHDRAW, TRANSFER
    }

    record Command(TransactionType type, String accountNumber, long amount) {}

    static class CommandParser {
        public static Command parse(String line) {
            String[] parts = line.split("\\s+");

            if (parts.length != 3) {
                throw new IllegalArgumentException(
                        "Invalid command format. Use: TYPE ACCOUNT AMOUNT");
            }

            TransactionType type = TransactionType.valueOf(parts[0].toUpperCase());
            String accountNumber = parts[1];
            long amount = Long.parseLong(parts[2]);

            return new Command(type, accountNumber, amount);
        }
    }

    static class StatementFormatter {
        public static String buildStatement(Account account) {
            StringBuilder sb = new StringBuilder();

            sb.append("=== Account Statement ===\n");
            sb.append("Account Number: ").append(account.getAccountNumber()).append("\n");
            sb.append("Owner: ").append(account.getOwnerName()).append("\n");
            sb.append("Balance: ₹").append(account.getBalance()).append("\n");
            sb.append("Interest Rate: ").append(account.interestRate()).append("%\n");
            sb.append("Status: ").append(account.isActive() ? "Active" : "Inactive").append("\n");

            return sb.toString();
        }
    }

    static class Customer implements Cloneable {

        private String name;
        private String email;
        private String mobile;
        private final String customerId;

        private static long customerCounter = 100;
        private Address address;

        public static class Address {
            private String line;
            private String city;
            private String pincode;

            public Address(String line, String city, String pincode) {
                this.line = line;
                this.city = city;
                this.pincode = pincode;
            }

            @Override
            public String toString() {
                return line + ", " + city + " - " + pincode;
            }
        }

        public Customer(String name,
                        String email,
                        String mobile,
                        Address address) {
            this.name = name;
            this.email = email;
            this.mobile = mobile;
            this.customerId = generateCustomerId();
            this.address = address;
        }

        private static String generateCustomerId() {
            customerCounter++;
            return "CUST" + customerCounter;
        }

        @Override
        public Customer clone() {
            try {
                return (Customer) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String toString() {
            return customerId + " | " + name + " | " +
                    email + " | " + mobile + " | " + address;
        }
    }

    /* ==========================
       ABSTRACT ACCOUNT CLASS
       ========================== */

    static abstract class Account {

        protected final String accountNumber;
        protected String ownerName;
        protected long balance;
        protected boolean active;

        private static int accountCounter = 1;

        public Account(String ownerName, long openingBalance) {
            this.ownerName = ownerName;
            this.balance = openingBalance;
            this.active = true;
            this.accountNumber = generateAccountNumber();
        }

        public Account(String ownerName) {
            this(ownerName, 0);
        }

        private static String generateAccountNumber() {
            return String.format("AC%04d", accountCounter++);
        }

        public void deposit(long amount) {
            if (amount > 0) {
                balance += amount;
            }
        }

        public boolean withdraw(long amount) {
            if (amount > 0 && canWithdraw(amount)) {
                balance -= amount;
                return true;
            }
            return false;
        }

        public abstract double interestRate();

        public abstract boolean canWithdraw(long amount);

        public String getAccountNumber() {
            return accountNumber;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public long getBalance() {
            return balance;
        }

        public boolean isActive() {
            return active;
        }

        @Override
        public String toString() {
            return accountNumber +
                    " | " +
                    ownerName +
                    " | Balance: ₹" +
                    balance;
        }

        @Override
        public boolean equals(Object o) {

            if (this == o)
                return true;

            if (!(o instanceof Account))
                return false;

            Account account = (Account) o;

            return accountNumber.equals(account.accountNumber);
        }

        @Override
        public int hashCode() {
            return accountNumber.hashCode();
        }
    }

    /* ==========================
       SAVINGS ACCOUNT
       ========================== */

    static class SavingsAccount extends Account {

        private long minBalance;

        public SavingsAccount(String ownerName,
                              long openingBalance,
                              long minBalance) {
            super(ownerName, openingBalance);
            this.minBalance = minBalance;
        }

        @Override
        public double interestRate() {
            return 4.0;
        }

        @Override
        public boolean canWithdraw(long amount) {
            return (balance - amount) >= minBalance;
        }

        public long getMinBalance() {
            return minBalance;
        }
    }

    /* ==========================
       CURRENT ACCOUNT
       ========================== */

    static class CurrentAccount extends Account {

        private long overdraftLimit;

        public CurrentAccount(String ownerName,
                              long openingBalance,
                              long overdraftLimit) {
            super(ownerName, openingBalance);
            this.overdraftLimit = overdraftLimit;
        }

        @Override
        public double interestRate() {
            return 0.0;
        }

        @Override
        public boolean canWithdraw(long amount) {
            return (balance - amount) >= (-overdraftLimit);
        }

        public long getOverdraftLimit() {
            return overdraftLimit;
        }
    }

    /* ==========================
       FIXED DEPOSIT ACCOUNT
       ========================== */

    static class FixedDepositAccount extends Account {

        public FixedDepositAccount(String ownerName,
                                   long openingBalance) {
            super(ownerName, openingBalance);
        }

        @Override
        public double interestRate() {
            return 7.0;
        }

        @Override
        public boolean canWithdraw(long amount) {
            return false;
        }
    }

    public static void main(String[] args) {

        Account[] accounts = new Account[3];

        accounts[0] = new SavingsAccount("Alice", 10000, 1000);
        accounts[1] = new CurrentAccount("Bob", 5000, 2000);
        accounts[2] = new FixedDepositAccount("Charlie", 20000);

        accounts[0].deposit(2000);
        accounts[1].withdraw(6000);
        accounts[2].withdraw(1000);

        System.out.println("=== Account Details ===");

        for (Account acc : accounts) {
            System.out.println(acc);
        }

        System.out.println("\n=== Runtime Polymorphism Demo ===");

        for (Account acc : accounts) {
            System.out.println(
                    acc.getOwnerName()
                            + " -> Interest Rate: "
                            + acc.interestRate()
                            + "%"
            );
        }

        System.out.println("\n=== Pattern instanceof Demo ===");

        for (Account acc : accounts) {

            if (acc instanceof CurrentAccount ca) {
                System.out.println(
                        ca.getOwnerName()
                                + " has overdraft limit ₹"
                                + ca.getOverdraftLimit()
                );
            }
        }

        System.out.println("\n=== Withdrawal Tests ===");

        System.out.println(
                "Savings Withdraw 9500 : "
                        + accounts[0].withdraw(9500));

        System.out.println(
                "Current Withdraw 1000 : "
                        + accounts[1].withdraw(1000));

        System.out.println(
                "FD Withdraw 500 : "
                        + accounts[2].withdraw(500));

        Customer.Address addr =
                new Customer.Address(
                        "123 Main Street",
                        "Sojitra",
                        "388460");

        Customer cust1 =
                new Customer(
                        "Dhanvi",
                        "dhanvi@example.com",
                        "9876543210",
                        addr);

        Customer cust2 = cust1.clone();

        System.out.println("\n=== Customer Details ===");
        System.out.println("Original: " + cust1);
        System.out.println("Cloned  : " + cust2);

        System.out.println("\n=== Validator Tests ===");

        System.out.println(
                "Valid Mobile: "
                        + Validator.isValidMobile("9876543210"));

        System.out.println(
                "Invalid Mobile: "
                        + Validator.isValidMobile("12345"));

        System.out.println(
                "Valid Email: "
                        + Validator.isValidEmail("test@example.com"));

        System.out.println(
                "Invalid Email: "
                        + Validator.isValidEmail("wrong-email"));

        System.out.println(
                "Valid PAN: "
                        + Validator.isValidPan("ABCDE1234F"));

        System.out.println(
                "Valid IFSC: "
                        + Validator.isValidIfsc("SBIN0001234"));

        System.out.println("\n=== Command Parsing ===");

        Command cmd =
                CommandParser.parse(
                        "DEPOSIT AC0001 500");

        System.out.println("Type      : " + cmd.type());
        System.out.println("Account   : " + cmd.accountNumber());
        System.out.println("Amount    : " + cmd.amount());

        System.out.println("\n=== Statement Formatter ===");

        System.out.println(
                StatementFormatter.buildStatement(
                        accounts[0]));
    }
}