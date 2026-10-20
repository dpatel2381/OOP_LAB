import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.*;

/* Practical 13: Generic repository with @Id reflection, saveAll and bounded generic method. */
public class MiniBank_prac13 {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Id { }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Positive {
        String message() default "must be > 0";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface MaxLength { int value(); }

    static class Account implements Comparable<Account> {
        @Id private final String accountNumber;
        @MaxLength(20) private final String ownerName;
        @Positive(message = "Balance must be greater than 0") private long balance;
        private final String accountType;
        private final double interestRate;

        Account(String accountNumber, String ownerName, long balance, String accountType, double interestRate) {
            this.accountNumber = accountNumber; this.ownerName = ownerName; this.balance = balance;
            this.accountType = accountType; this.interestRate = interestRate;
        }
        public String getAccountNumber() { return accountNumber; }
        public String getOwnerName() { return ownerName; }
        public synchronized long getBalance() { return balance; }
        public String getAccountType() { return accountType; }
        public double interestRate() { return interestRate; }
        public synchronized void deposit(long amount) {
            if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
            balance += amount;
        }
        public synchronized boolean withdraw(long amount) {
            if (amount <= 0 || amount > balance) return false;
            balance -= amount; return true;
        }
        @Override public int compareTo(Account other) { return accountNumber.compareTo(other.accountNumber); }
        @Override public String toString() {
            return accountNumber + " | " + ownerName + " | " + accountType + " | Balance: ₹" + getBalance();
        }
    }

    static class Customer {
        @Id private final String customerId;
        @MaxLength(40) private final String name;
        private final String email;
        Customer(String customerId, String name, String email) {
            this.customerId = customerId; this.name = name; this.email = email;
        }
        @Override public String toString() { return customerId + " | " + name + " | " + email; }
    }

    static class Repository<T> {
        private final HashMap<String, T> items = new HashMap<>();

        public T save(T entity) {
            if (entity == null) throw new IllegalArgumentException("Entity cannot be null.");
            String id = readId(entity);
            items.put(id, entity);
            return entity;
        }

        private String readId(T entity) {
            Class<?> type = entity.getClass();
            while (type != null) {
                for (Field field : type.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Id.class)) {
                        try {
                            field.setAccessible(true);
                            Object value = field.get(entity);
                            if (value == null || value.toString().isBlank())
                                throw new IllegalArgumentException("@Id field cannot be empty.");
                            return value.toString();
                        } catch (IllegalAccessException e) {
                            throw new IllegalStateException("Cannot read @Id field.", e);
                        }
                    }
                }
                type = type.getSuperclass();
            }
            throw new IllegalArgumentException("Entity has no field annotated with @Id: " + entity.getClass().getSimpleName());
        }

        public T findById(String id) { return items.get(id); }
        public ArrayList<T> findAll() { return new ArrayList<>(items.values()); }
        public boolean delete(String id) { return items.remove(id) != null; }

        @SafeVarargs
         public final void saveAll(T... entities) {
            for (T entity : entities) save(entity);
        }
        public int size() { return items.size(); }
    }

    static <N extends Number> double average(N[] values) {
        if (values == null || values.length == 0) throw new IllegalArgumentException("Array cannot be empty.");
        double total = 0;
        for (N value : values) total += value.doubleValue();
        return total / values.length;
    }

    public static void main(String[] args) {
        Repository<Account> accounts = new Repository<>();
        Account a1 = new Account("ACC1001", "Dhanvi", 15000, "Savings", 4.0);
        Account a2 = new Account("ACC1002", "Maya", 7000, "Current", 0.0);
        Account a3 = new Account("ACC1003", "Prit", 10000, "Fixed Deposit", 7.0);
        accounts.saveAll(a1, a2, a3);

        System.out.println("=== MiniBank Practical 13 ===");
        System.out.println("Find ACC1002: " + accounts.findById("ACC1002"));
        System.out.println("\nAll accounts:");
        for (Account account : accounts.findAll()) System.out.println(account);
        System.out.println("Repository size: " + accounts.size());

        Repository<Customer> customers = new Repository<>();
        customers.saveAll(
            new Customer("CUST101", "Dhanvi", "dhanvi@example.com"),
            new Customer("CUST102", "Maya", "maya@example.com")
        );
        System.out.println("\nCustomers using the same generic repository:");
        for (Customer customer : customers.findAll()) System.out.println(customer);

        System.out.println("\nAverage sample balance: " + average(new Long[]{15000L, 7000L, 10000L}));
        System.out.println("Delete ACC1002: " + accounts.delete("ACC1002"));
    }
}
