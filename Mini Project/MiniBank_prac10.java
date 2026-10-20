import static java.lang.Math.max;
import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class MiniBank_prac10
{
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Id
    {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Positive
    {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
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
    }

    interface WithdrawRule
    {
        boolean canWithdraw(long amount);
    }

    interface Premium
    {
    }

    static class Validator
    {
        private static final Pattern MOBILE = Pattern.compile("^[6-9][0-9]{9}$");
        private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
        private static final Pattern PAN = Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]$");
        private static final Pattern IFSC = Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");

        static boolean isMobile(String value)
        {
            return MOBILE.matcher(value).matches();
        }

        static boolean isEmail(String value)
        {
            return EMAIL.matcher(value).matches();
        }

        static boolean isPAN(String value)
        {
            return PAN.matcher(value).matches();
        }

        static boolean isIFSC(String value)
        {
            return IFSC.matcher(value).matches();
        }
    }

    static class AnnotationValidator
    {
        static boolean validate(Object object)
        {
            boolean valid = true;

            for (Field field : object.getClass().getDeclaredFields())
            {
                try
                {
                    field.setAccessible(true);
                    Object value = field.get(object);

                    if (field.isAnnotationPresent(Id.class))
                    {
                        if (value == null || value.toString().isEmpty())
                        {
                            valid = false;
                        }
                    }

                    if (field.isAnnotationPresent(Positive.class))
                    {
                        if (value instanceof Number && ((Number) value).doubleValue() <= 0)
                        {
                            valid = false;
                        }
                    }

                    if (field.isAnnotationPresent(MaxLength.class))
                    {
                        if (value != null && value.toString().length() > field.getAnnotation(MaxLength.class).value())
                        {
                            valid = false;
                        }
                    }
                }
                catch (Exception e)
                {
                    valid = false;
                }
            }

            return valid;
        }
    }

    enum TransactionType
    {
        DEPOSIT,
        WITHDRAW
    }

    record Command(TransactionType type, String accountNumber, long amount)
    {
    }

    static class CommandParser
    {
        static Command parse(String line)
        {
            String[] parts = line.split("\\s+");

            TransactionType type = TransactionType.valueOf(parts[0]);
            String accountNumber = parts[1];
            long amount = Long.parseLong(parts[2]);

            return new Command(type, accountNumber, amount);
        }
    }

    static class StatementFormatter
    {
        static String format(String accountNumber, String type, long amount)
        {
            return accountNumber + " | " + type + " | " + amount;
        }
    }

    static class Customer implements Cloneable
    {
        private String name;
        private Address address;

        Customer(String name, Address address)
        {
            this.name = name;
            this.address = address;
        }

        static class Address
        {
            private String city;

            Address(String city)
            {
                this.city = city;
            }

            public String getCity()
            {
                return city;
            }

            public void setCity(String city)
            {
                this.city = city;
            }

            @Override
            public String toString()
            {
                return city;
            }
        }

        public Address getAddress()
        {
            return address;
        }

        @Override
        protected Customer clone()
        {
            try
            {
                Customer copy = (Customer) super.clone();
                copy.address = new Address(address.city);
                return copy;
            }
            catch (CloneNotSupportedException e)
            {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String toString()
        {
            return name + " - " + address;
        }
    }

    static abstract class Account implements Transactable, InterestBearing, WithdrawRule
    {
        @Id
        protected String accountNumber;

        @MaxLength(50)
        protected String holderName;

        @Positive
        protected long balance;

        Account(String holderName, long balance, int accountNumber)
        {
            this.holderName = holderName;
            this.balance = balance;
            this.accountNumber = "ACC" + (1000 + accountNumber);
        }

        public String getAccountNumber()
        {
            return accountNumber;
        }

        public String getHolderName()
        {
            return holderName;
        }

        public long balance()
        {
            return balance;
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
            if (canWithdraw(amount))
            {
                balance -= amount;
                return true;
            }

            return false;
        }

        @Override
        public boolean canWithdraw(long amount)
        {
            return amount > 0 && amount <= balance;
        }

        @Override
        public boolean equals(Object object)
        {
            if (this == object)
            {
                return true;
            }

            if (!(object instanceof Account))
            {
                return false;
            }

            Account other = (Account) object;

            return accountNumber.equals(other.accountNumber);
        }

        @Override
        public String toString()
        {
            return accountNumber + " | " + holderName + " | " + balance;
        }
    }

    static class SavingsAccount extends Account implements Premium
    {
        SavingsAccount(String holderName, long balance, int accountNumber)
        {
            super(holderName, balance, accountNumber);
        }

        @Override
        public double interestRate()
        {
            return 0.04;
        }
    }

    static class CurrentAccount extends Account
    {
        CurrentAccount(String holderName, long balance, int accountNumber)
        {
            super(holderName, balance, accountNumber);
        }

        @Override
        public double interestRate()
        {
            return 0.02;
        }

        @Override
        public boolean canWithdraw(long amount)
        {
            return amount > 0 && amount <= balance + 1000;
        }
    }

    static class FixedDepositAccount extends Account
    {
        FixedDepositAccount(String holderName, long balance, int accountNumber)
        {
            super(holderName, balance, accountNumber);
        }

        @Override
        public double interestRate()
        {
            return 0.07;
        }

        @Override
        public boolean canWithdraw(long amount)
        {
            return false;
        }
    }

    static class AccountWorker implements Runnable
    {
        private Account account;
        private int times;
        private long amount;

        AccountWorker(Account account, int times, long amount)
        {
            this.account = account;
            this.times = times;
            this.amount = amount;
        }

        @Override
        public void run()
        {
            for (int i = 0; i < times; i++)
            {
                synchronized (account)
                {
                    account.deposit(amount);
                }
            }

            System.out.println(Thread.currentThread().getName() + " completed");
        }
    }

    static class TransactionProcessor
    {
        private ExecutorService executor;

        TransactionProcessor()
        {
            executor = Executors.newFixedThreadPool(4);
        }

        public void submit(Runnable task)
        {
            executor.execute(task);
        }

        public void stop() throws InterruptedException
        {
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    static class Transaction
    {
        String type;
        Account account;
        long amount;

        Transaction(String type, Account account, long amount)
        {
            this.type = type;
            this.account = account;
            this.amount = amount;
        }
    }

    static class TransactionBuffer
    {
        private Transaction[] buffer = new Transaction[5];
        private int count = 0;

        public synchronized void add(Transaction transaction) throws InterruptedException
        {
            while (count == buffer.length)
            {
                wait();
            }

            buffer[count] = transaction;
            count++;

            System.out.println("Produced: " + transaction.type + " " + transaction.amount);

            notify();
        }

        public synchronized Transaction remove() throws InterruptedException
        {
            while (count == 0)
            {
                wait();
            }

            Transaction transaction = buffer[0];

            for (int i = 1; i < count; i++)
            {
                buffer[i - 1] = buffer[i];
            }

            count--;

            System.out.println("Consumed: " + transaction.type + " " + transaction.amount);

            notify();

            return transaction;
        }
    }

    static void transfer(Account from, Account to, long amount)
    {
        Account first;
        Account second;

        if (from.getAccountNumber().compareTo(to.getAccountNumber()) < 0)
        {
            first = from;
            second = to;
        }
        else
        {
            first = to;
            second = from;
        }

        synchronized (first)
        {
            synchronized (second)
            {
                if (from.withdraw(amount))
                {
                    to.deposit(amount);

                    System.out.println("Transfer completed: " + from.getAccountNumber() + " -> " + to.getAccountNumber() + " | Amount: " + amount);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception
    {
        System.out.println("MiniBank Practical 10");

        Account savings = new SavingsAccount("Dhanvi", 15000, 1);
        Account current = new CurrentAccount("Maya", 7000, 2);
        Account fixed = new FixedDepositAccount("Prit", 10000, 3);

        savings.deposit(1000);
        savings.withdraw(500);

        current.deposit(2000);
        current.withdraw(1000);

        System.out.println(savings);
        System.out.println(current);
        System.out.println(fixed);

        System.out.println("Savings Interest Rate: " + savings.interestRate());
        System.out.println("Current Interest Rate: " + current.interestRate());
        System.out.println("Fixed Deposit Interest Rate: " + fixed.interestRate());

        Transactable transaction = new SavingsAccount("Anonymous", 1000, 4);
        transaction.deposit(500);

        System.out.println("Anonymous Account Balance: " + ((Account) transaction).balance());

        Transactable lambdaTransaction = new Transactable()
        {
            private long balance = 1000;

            @Override
            public void deposit(long amount)
            {
                balance += amount;
            }

            @Override
            public boolean withdraw(long amount)
            {
                if (amount <= balance)
                {
                    balance -= amount;
                    return true;
                }

                return false;
            }
        };

        lambdaTransaction.deposit(500);

        System.out.println("Lambda/Anonymous Balance: " + ((Account) savings).balance() );

        if (savings instanceof Premium)
        {
            System.out.println("Savings account is Premium");
        }

        Account anotherSavings = new SavingsAccount("Dhanvi", 15000, 1);

        System.out.println("Accounts Equal: " + savings.equals(anotherSavings));

        System.out.println("Mobile Valid: " + Validator.isMobile("9876543210"));
        System.out.println("Email Valid: " + Validator.isEmail("test@gmail.com"));
        System.out.println("PAN Valid: " + Validator.isPAN("ABCDE1234F"));
        System.out.println("IFSC Valid: " + Validator.isIFSC("SBIN0001234"));

        Customer customer1 = new Customer("Dhanvi", new Customer.Address("Anand")
        );

        Customer customer2 = customer1.clone();

        customer2.getAddress().setCity("Ahmedabad");

        System.out.println("Original Customer: " + customer1);
        System.out.println("Cloned Customer: " + customer2);

        Command command = CommandParser.parse("DEPOSIT ACC1001 5000");

        System.out.println("Command Type: " + command.type());
        System.out.println("Command Account: " + command.accountNumber());
        System.out.println("Command Amount: " + command.amount());

        System.out.println(StatementFormatter.format("ACC1001","DEPOSIT",5000));

        System.out.println("Annotation Validation: " + AnnotationValidator.validate(savings));

        System.out.println("Maximum Value: " + max(100, 200));

        System.out.println("\nPractical 10 Part B - Multithreading");

        Account threadAccount = new SavingsAccount("Thread User", 0, 0);

        Thread[] threads = new Thread[10];

        for (int i = 0; i < 10; i++)
        {
            AccountWorker worker = new AccountWorker(threadAccount, 1000, 1);

            threads[i] = new Thread(worker);
            threads[i].setName("Thread-" + (i + 1));
            threads[i].start();
        }

        for (int i = 0; i < 10; i++)
        {
            threads[i].join();
        }

        System.out.println("Final Balance: " + threadAccount.balance());

        System.out.println("\nPractical 10 Part B - Thread Pool Transactions");

        TransactionProcessor processor = new TransactionProcessor();

        processor.submit(() ->
        {
            threadAccount.deposit(500);
        });

        processor.submit(() ->
        {
            threadAccount.deposit(300);
        });

        processor.submit(() ->
        {
            threadAccount.withdraw(100);
        });

        processor.submit(() ->
        {
            threadAccount.deposit(200);
        });

        processor.submit(() ->
        {
            threadAccount.withdraw(100);
        });

        processor.submit(() ->
        {
            threadAccount.deposit(100);
        });

        processor.stop();

        System.out.println("Balance after thread pool transactions: " + threadAccount.balance()
        );

        System.out.println("\nPractical 10 Part B - Producer Consumer");

        TransactionBuffer transactionBuffer = new TransactionBuffer();

        Thread producer = new Thread(() ->
        {
            try
            {
                transactionBuffer.add(
                    new Transaction("DEPOSIT", threadAccount, 100)
                );

                transactionBuffer.add(
                    new Transaction("DEPOSIT", threadAccount, 200)
                );

                transactionBuffer.add(
                    new Transaction("WITHDRAW", threadAccount, 50)
                );

                transactionBuffer.add(
                    new Transaction("DEPOSIT", threadAccount, 300)
                );

                transactionBuffer.add(
                    new Transaction("WITHDRAW", threadAccount, 100)
                );
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() ->
    {
    try
    {
        for (int i = 0; i < 5; i++)
        {
            Transaction currentTransaction = transactionBuffer.remove();

            synchronized (currentTransaction.account)
            {
                if (currentTransaction.type.equals("DEPOSIT"))
                {
                    currentTransaction.account.deposit(currentTransaction.amount);
                }
                else
                {
                    currentTransaction.account.withdraw(currentTransaction.amount);
                }
            }
        }
    }
    catch (InterruptedException e)
    {
        Thread.currentThread().interrupt();
    }
    });
           

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        System.out.println("Balance after producer-consumer: " + threadAccount.balance()
        );

        System.out.println("\nPractical 10 Part B - Deadlock Safe Transfer");

        Account account1 = new SavingsAccount("User 1", 5000, 7);
        Account account2 = new SavingsAccount("User 2", 5000, 8);

        Thread transfer1 = new Thread(() ->
        {
            transfer(account1, account2, 500);
        });

        Thread transfer2 = new Thread(() ->
        {
            transfer(account2, account1, 300);
        });

        transfer1.start();
        transfer2.start();

        transfer1.join();
        transfer2.join();

        System.out.println("Account 1 Balance: " + account1.balance());

        System.out.println("Account 2 Balance: " + account2.balance());
    }
}
