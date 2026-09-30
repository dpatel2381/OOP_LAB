import java.util.regex.Pattern;
import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import static java.lang.Math.max;

public class MiniBank_prac8 {

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Id {}

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Positive 
    {
        String message() default "must be > 0";
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface MaxLength 
    {
        int value();
    }

    interface Transactable 
    {
        void deposit(long amount);
        boolean withdraw(long amount);
    }

    interface InterestBearing 
    {
        double interestRate();
        long balance();
        default double yearlyInterest() 
        {
            return interestRate() * balance() / 100;
        }
    }

    @FunctionalInterface
    interface WithdrawRule
    {
        boolean allow(Account account, long amount);
    }

    interface Premium {}

    static class Validator 
    {
        static final Pattern MOBILE = Pattern.compile("\\d{10}");
        static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
        static final Pattern PAN = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]");
        static final Pattern IFSC = Pattern.compile("[A-Z]{4}0[A-Z0-9]{6}");

        static boolean isMobileValid(String mobile) 
        {
            return MOBILE.matcher(mobile).matches();
        }

        static boolean isEmailValid(String email) 
        {
            return EMAIL.matcher(email).matches();
        }

        static boolean isPANValid(String pan) 
        {
            return PAN.matcher(pan).matches();
        }

        static boolean isIFSCValid(String ifsc) 
        {
            return IFSC.matcher(ifsc).matches();
        }
    }

    static class AnnotationValidator 
    {
        public static String[] validate(Object obj) 
        {
            ArrayList<String> errors = new ArrayList<>();
            Class<?> currentClass = obj.getClass();

            while (currentClass != null) 
                {
                Field[] fields = currentClass.getDeclaredFields();

                for (Field field : fields) 
                    {
                    try 
                    {
                        field.setAccessible(true);
                        Object value = field.get(obj);

                        if (field.isAnnotationPresent(Positive.class)) 
                            {
                            Positive annotation = field.getAnnotation(Positive.class);

                            if (value instanceof Number) 
                                {
                                Number number = (Number) value;
                                if (number.doubleValue() <= 0) 
                                    {
                                    errors.add(field.getName() + ": " + annotation.message());
                                }
                            }
                        }

                        if (field.isAnnotationPresent(MaxLength.class)) 
                            {
                            MaxLength annotation = field.getAnnotation(MaxLength.class);

                            if (value != null && value.toString().length() > annotation.value()) 
                            {
                                errors.add(field.getName() + ": maximum length is " + annotation.value());
                            }
                        }
                    } 
                    catch (Exception e) 
                    {
                        errors.add("Error reading field: " + field.getName());
                    }
                }
                currentClass = currentClass.getSuperclass();
            }
            return errors.toArray(new String[0]);
        }
    }

    enum TransactionType
    {
        DEPOSIT, WITHDRAW, TRANSFER
    }

    static record Command(TransactionType type, String accountNumber, long amount) {}

    static class CommandParser 
    {
        static Command parse(String line) 
        {
            String[] parts = line.split("\\s+");

            TransactionType type = TransactionType.valueOf(parts[0].toUpperCase());
            String accountNumber = parts[1];
            long amount = Long.parseLong(parts[2]);

            return new Command(type, accountNumber, amount);
        }
    }

    static class StatementFormatter 
    {
        static String buildStatement(Account account) 
        {
            return "Account Number: " + account.getAccountNumber() + "\nOwner: " + account.getOwnerName() + "\nBalance: " + account.balance() + "\nInterest Rate: " + account.interestRate() + "%";
        }
    }

    static class Customer implements Cloneable 
    {
        static class Address 
        {
            private String city;
            private String state;

            Address(String city, String state) 
            {
                this.city = city;
                this.state = state;
            }

            @Override
            public String toString() 
            {
                return city + ", " + state;
            }
        }

        private String name;
        private Address address;

        Customer(String name, Address address) 
        {
            this.name = name;
            this.address = address;
        }

        @Override
        public Customer clone() 
        {
            try 
            {
                Customer copy = (Customer) super.clone();
                copy.address = new Address(address.city, address.state);
                return copy;
            } 
            catch (CloneNotSupportedException e) 
            {
                throw new AssertionError();
            }
        }

        @Override
        public String toString() 
        {
            return "Customer{name='" + name + "', address=" + address + "}";
        }

        public String getName() 
        {
            return name;
        }

        public Address getAddress()
        {
            return address;
        }
    }

    static abstract class Account implements Transactable, InterestBearing 
    {

        private static int nextNumber = 1001;

        @Id
        private final String accountNumber;

        @MaxLength(20)
        private String ownerName;

        @Positive(message = "Balance must be greater than 0")
        private long balance;

        private boolean active;

        Account(String ownerName, long balance) 
        {
            this.accountNumber = "ACC" + nextNumber++;
            this.ownerName = ownerName;
            this.balance = balance;
            this.active = true;
        }

        @Override
        public void deposit(long amount) 
        {
            if (amount > 0) 
            {
                balance += amount;
            }
        }

        @Override
        public boolean withdraw(long amount) 
        {
            if (amount > 0 && canWithdraw(amount)) 
            {
                balance -= amount;
                return true;
            }
            return false;
        }

        abstract boolean canWithdraw(long amount);

        @Override
        public long balance(){ return balance;}
        public String getAccountNumber() {return accountNumber;}
        public String getOwnerName() { return ownerName;}
        public boolean isActive() { return active;}

        @Override
        public String toString() 
        {
            return "Account{accountNumber='" + accountNumber + "', ownerName='" + ownerName + "', balance=" + balance + ", active=" + active + "}";
        }

        @Override
        public boolean equals(Object obj) 
        {
            if (this == obj) { return true;}
            if (!(obj instanceof Account)) { return false;}

            Account other = (Account) obj;
            return accountNumber.equals(other.accountNumber);
        }

        @Override
        public int hashCode()
        {
            return accountNumber.hashCode();
        }
    }

    static class SavingsAccount extends Account implements Premium 
    {

        private long minBalance;

        SavingsAccount(String ownerName, long balance, long minBalance) 
        {
            super(ownerName, balance);
            this.minBalance = minBalance;
        }

        @Override
        public double interestRate() {return 4.0;}

        @Override
        boolean canWithdraw(long amount)
        {
            return balance() - amount >= minBalance;
        }

        @Override
        public String toString() 
        {
            return "SavingsAccount{" + super.toString() + ", minBalance=" + minBalance + "}";
        }
    }

    static class CurrentAccount extends Account 
    {

        private long overdraftLimit;

        CurrentAccount(String ownerName, long balance, long overdraftLimit) 
        {
            super(ownerName, balance);
            this.overdraftLimit = overdraftLimit;
        }

        @Override
        public double interestRate()  { return 0.0;}

        @Override
        boolean canWithdraw(long amount) 
        {
            return balance() - amount >= -overdraftLimit;
        }

        @Override
        public String toString() 
        {
            return "CurrentAccount{" + super.toString() + ", overdraftLimit=" + overdraftLimit + "}";
        }
    }

    static class FixedDepositAccount extends Account 
    {

        FixedDepositAccount(String ownerName, long balance)
        {
            super(ownerName, balance);
        }

        @Override
        public double interestRate() { return 7.0;}

        @Override
        boolean canWithdraw(long amount) 
        {
            return false;
        }

        @Override
        public String toString() 
        {
            return "FixedDepositAccount{" + super.toString() + "}";
        }
    }

    public static void main(String[] args) 
    {

        Account savings = new SavingsAccount("Dhanvi", 10000, 2000);
        Account current = new CurrentAccount("Rahul", 5000, 3000);
        Account fixed = new FixedDepositAccount("Amit", 20000);

        Account[] accounts = {savings, current, fixed};

        for (Account account : accounts) 
        {
            account.deposit(1000);
            System.out.println(account);
            System.out.println("Interest Rate: " + account.interestRate() + "%");
            System.out.println("Yearly Interest: " + account.yearlyInterest());
            System.out.println();
        }

        System.out.println("Withdrawal:");

        WithdrawRule rule1 = new WithdrawRule() 
        {
            @Override
            public boolean allow(Account account, long amount) 
            {
                return account.withdraw(amount);
            }
        };

        System.out.println("Anonymous Class: " + rule1.allow(savings, 3000));

        WithdrawRule rule2 = (account, amount) -> account.withdraw(amount);

        System.out.println("Lambda: " + rule2.allow(current, 2000));

        if (savings instanceof SavingsAccount savingsAccount) 
        {
            System.out.println("Savings Minimum Balance Rule Applied");
            System.out.println("Balance: " + savingsAccount.balance());
        }

        if (savings instanceof Premium) 
        {
            System.out.println("Premium Account");
        }

        Account anotherSavings = new SavingsAccount("Dhanvi", 10000, 2000);

        System.out.println("Account Equality: " + savings.equals(anotherSavings));

        System.out.println("Mobile Valid: " + Validator.isMobileValid("9876543210"));
        System.out.println("Email Valid: " + Validator.isEmailValid("dhanvi@gmail.com"));
        System.out.println("PAN Valid: " + Validator.isPANValid("ABCDE1234F"));
        System.out.println("IFSC Valid: " + Validator.isIFSCValid("SBIN0123456"));

        Customer.Address address = new Customer.Address("Anand", "Gujarat");
        Customer customer = new Customer("Dhanvi", address);
        Customer clonedCustomer = customer.clone();

        System.out.println(customer);
        System.out.println(clonedCustomer);

        Command command = CommandParser.parse("DEPOSIT ACC1001 5000");

        System.out.println("Command Type: " + command.type());
        System.out.println("Command Account: " + command.accountNumber());
        System.out.println("Command Amount: " + command.amount());

        System.out.println(StatementFormatter.buildStatement(savings));

        Account invalidAccount = new SavingsAccount("InvalidAccount", -5000, 1000);
        String[] errors = AnnotationValidator.validate(invalidAccount);

        System.out.println("\nAnnotation Validation:");

        if (errors.length == 0) 
        {
            System.out.println("Account is valid.");
        } 
        else 
        {
            for (String error : errors) 
            {
                System.out.println("Error: " + error);
            }
        }
        String[] validErrors = AnnotationValidator.validate(savings);

        if (validErrors.length == 0) 
        {
            System.out.println("Valid Account Passed Validation.");
        }

        int maximum = max(100, 500);
        System.out.println("Maximum Value: " + maximum);
    }
}
