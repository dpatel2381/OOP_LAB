import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class MiniBank_prac14 {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Id { }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Positive { String message() default "must be > 0"; }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface MaxLength { int value(); }

    static class Account implements Serializable, Comparable<Account> {
        private static final long serialVersionUID = 1L;
        @Id private final String accountNumber;
        @MaxLength(50) private final String holderName;
        @Positive(message = "Balance must be positive") private long balance;
        private final String accountType;
        private final double interestRate;

        Account(String number, String name, long balance, String type, double rate) {
            if (number == null || number.isBlank()) throw new IllegalArgumentException("Account number is required.");
            if (name == null || name.isBlank()) throw new IllegalArgumentException("Owner name is required.");
            if (balance < 0) throw new IllegalArgumentException("Opening balance cannot be negative.");
            this.accountNumber = number; this.holderName = name; this.balance = balance;
            this.accountType = type; this.interestRate = rate;
        }
        public String getAccountNumber() { return accountNumber; }
        public String getHolderName() { return holderName; }
        public synchronized long getBalance() { return balance; }
        public String getAccountType() { return accountType; }
        public double interestRate() { return interestRate; }
        public synchronized void deposit(long amount) {
            if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive.");
            balance = Math.addExact(balance, amount);
        }
        public synchronized void withdraw(long amount) {
            if (amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive.");
            if (amount > balance) throw new IllegalArgumentException("Insufficient balance.");
            balance -= amount;
        }
        @Override public int compareTo(Account other) { return accountNumber.compareTo(other.accountNumber); }
        @Override public String toString() {
            return accountNumber + " | " + holderName + " | " + accountType + " | Balance: ₹" + getBalance()
                    + " | Interest: " + interestRate + "%";
        }
    }

    static class Validator {
        private static final java.util.regex.Pattern MOBILE = java.util.regex.Pattern.compile("^[6-9][0-9]{9}$");
        private static final java.util.regex.Pattern EMAIL = java.util.regex.Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
        private static final java.util.regex.Pattern PAN = java.util.regex.Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]$");
        private static final java.util.regex.Pattern IFSC = java.util.regex.Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");
        public static boolean isValidMobile(String value) { return value != null && MOBILE.matcher(value).matches(); }
        public static boolean isValidEmail(String value) { return value != null && EMAIL.matcher(value).matches(); }
        public static boolean isValidPan(String value) { return value != null && PAN.matcher(value).matches(); }
        public static boolean isValidIfsc(String value) { return value != null && IFSC.matcher(value).matches(); }
    }

    static class AnnotationValidator {
        static List<String> validate(Object object) {
            List<String> errors = new ArrayList<>();
            Class<?> type = object.getClass();
            while (type != null) {
                for (Field field : type.getDeclaredFields()) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(object);
                        if (field.isAnnotationPresent(Id.class) && (value == null || value.toString().isBlank()))
                            errors.add(field.getName() + ": ID is required");
                        if (field.isAnnotationPresent(Positive.class) && value instanceof Number
                                && ((Number) value).doubleValue() < 0)
                            errors.add(field.getName() + ": " + field.getAnnotation(Positive.class).message());
                        if (field.isAnnotationPresent(MaxLength.class) && value != null
                                && value.toString().length() > field.getAnnotation(MaxLength.class).value())
                            errors.add(field.getName() + ": maximum length is " + field.getAnnotation(MaxLength.class).value());
                    } catch (ReflectiveOperationException | RuntimeException ex) {
                        errors.add("Unable to validate field " + field.getName());
                    }
                }
                type = type.getSuperclass();
            }
            return errors;
        }
    }

    static class Repository<T> {
        private final ConcurrentHashMap<String, T> items = new ConcurrentHashMap<>();
        public T save(T entity) { items.put(readId(entity), entity); return entity; }
        private String readId(T entity) {
            for (Class<?> c = entity.getClass(); c != null; c = c.getSuperclass()) {
                for (Field f : c.getDeclaredFields()) {
                    if (f.isAnnotationPresent(Id.class)) {
                        try {
                            f.setAccessible(true);
                            Object id = f.get(entity);
                            if (id == null || id.toString().isBlank()) throw new IllegalArgumentException("ID is empty.");
                            return id.toString();
                        } catch (IllegalAccessException e) { throw new IllegalStateException(e); }
                    }
                }
            }
            throw new IllegalArgumentException("Entity must have an @Id field.");
        }
        public T findById(String id) { return items.get(id); }
        public ArrayList<T> findAll() { return new ArrayList<>(items.values()); }
        public boolean delete(String id) { return items.remove(id) != null; }
        public int size() { return items.size(); }
    }

    static class StatePersister {
        static void save(List<Account> accounts, Path file) throws IOException {
            Path parent = file.toAbsolutePath().getParent();
            if (parent != null) Files.createDirectories(parent);
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
                out.writeObject(new ArrayList<>(accounts));
            }
        }
        @SuppressWarnings("unchecked")
        static List<Account> load(Path file) throws IOException, ClassNotFoundException {
            if (!Files.exists(file)) return new ArrayList<>();
            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
                return (List<Account>) in.readObject();
            }
        }
    }

    static class TransactionLog {
        static void append(String text) {
            try {
                Path file = Paths.get("logs", "transactions.log");
                Files.createDirectories(file.toAbsolutePath().getParent());
                Files.write(file, (LocalDateTime.now() + " | " + text + System.lineSeparator()).getBytes(java.nio.charset.StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) { System.err.println("Could not write transaction log: " + e.getMessage()); }
        }
    }

    static class BankService {
        private final Repository<Account> repository = new Repository<>();
        private final Path dataFile = Paths.get("data", "accounts.dat");
        BankService() {
            try {
                for (Account account : StatePersister.load(dataFile)) repository.save(account);
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("No saved accounts loaded: " + e.getMessage());
            }
            if (repository.size() == 0) {
                repository.save(new Account("ACC1001", "Dhanvi", 15000, "Savings", 4.0));
                repository.save(new Account("ACC1002", "Maya", 7000, "Current", 0.0));
                repository.save(new Account("ACC1003", "Prit", 10000, "Fixed Deposit", 7.0));
            }
        }
        Account openAccount(String owner, long openingBalance, String type, double rate) {
            String number = "ACC" + (1000 + repository.size() + 1);
            if (repository.findById(number) != null) number = "ACC" + System.currentTimeMillis();
            Account account = new Account(number, owner, openingBalance, type, rate);
            List<String> errors = AnnotationValidator.validate(account);
            if (!errors.isEmpty()) throw new IllegalArgumentException(String.join(", ", errors));
            repository.save(account); TransactionLog.append("OPEN | " + number + " | " + openingBalance);
            return account;
        }
        Account requireAccount(String number) {
            Account account = repository.findById(number);
            if (account == null) throw new IllegalArgumentException("Account not found: " + number);
            return account;
        }
        void deposit(String number, long amount) {
            Account account = requireAccount(number); account.deposit(amount);
            TransactionLog.append("DEPOSIT | " + number + " | " + amount);
        }
        void withdraw(String number, long amount) {
            Account account = requireAccount(number); account.withdraw(amount);
            TransactionLog.append("WITHDRAW | " + number + " | " + amount);
        }
        void transfer(String fromNumber, String toNumber, long amount) {
            if (fromNumber.equals(toNumber)) throw new IllegalArgumentException("Source and destination must differ.");
            Account from = requireAccount(fromNumber), to = requireAccount(toNumber);
            Account first = fromNumber.compareTo(toNumber) < 0 ? from : to;
            Account second = first == from ? to : from;
            synchronized (first) {
                synchronized (second) {
                    from.withdraw(amount);
                    try { to.deposit(amount); }
                    catch (RuntimeException e) { from.deposit(amount); throw e; }
                }
            }
            TransactionLog.append("TRANSFER | " + fromNumber + " -> " + toNumber + " | " + amount);
        }
        String statement(String number) {
            Account a = requireAccount(number);
            return "=== Account Statement ===\nAccount Number: " + a.getAccountNumber()
                    + "\nOwner: " + a.getHolderName() + "\nType: " + a.getAccountType()
                    + "\nBalance: ₹" + a.getBalance() + "\nInterest Rate: " + a.interestRate() + "%";
        }
        void listAccounts() {
            ArrayList<Account> all = repository.findAll();
            all.sort(Comparator.comparing(Account::getAccountNumber));
            for (Account a : all) System.out.println(a);
        }
        void save() {
            try { StatePersister.save(repository.findAll(), dataFile); }
            catch (IOException e) { System.err.println("Could not save accounts: " + e.getMessage()); }
        }
        void report() {
            Path logs = Paths.get("logs"), report = Paths.get("reports", "end-of-day-report.txt");
            long deposits = 0, withdrawals = 0;
            StringBuilder out = new StringBuilder("=== MiniBank Report ===\n");
            try {
                Files.createDirectories(logs);
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(logs, "*.log")) {
                    for (Path file : stream) {
                        out.append("\nFile: ").append(file.getFileName())
                           .append(" | Size: ").append(Files.size(file))
                           .append(" | Modified: ").append(Files.getLastModifiedTime(file)).append('\n');
                        for (String line : Files.readAllLines(file)) {
                            out.append(line).append('\n');
                            String[] p = line.split("\\|");
                            for (int i = 0; i < p.length; i++) {
                                String action = p[i].trim();
                                if ((action.equals("DEPOSIT") || action.equals("WITHDRAW")) && i + 1 < p.length) {
                                    try {
                                        long amount = Long.parseLong(p[i + 1].trim());
                                        if (action.equals("DEPOSIT")) deposits += amount;
                                        else withdrawals += amount;
                                    } catch (NumberFormatException ignored) { }
                                }
                            }
                        }
                    }
                }
                out.append("\nTotal deposits: ₹").append(deposits)
                   .append("\nTotal withdrawals: ₹").append(withdrawals).append('\n');
                Files.createDirectories(report.toAbsolutePath().getParent());
                Files.write(report, out.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                System.out.println(out);
                System.out.println("Report saved to " + report);
            } catch (IOException e) { System.out.println("Report could not be generated: " + e.getMessage()); }
        }
        void batchDeposit(String number, long amount, int count) throws InterruptedException {
            Account account = requireAccount(number);
            ExecutorService pool = Executors.newFixedThreadPool(Math.min(count, 8));
            for (int i = 0; i < count; i++) pool.submit(() -> account.deposit(amount));
            pool.shutdown();
            if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                pool.shutdownNow(); throw new IllegalStateException("Batch timed out.");
            }
            TransactionLog.append("BATCH_DEPOSIT | " + number + " | " + (amount * count));
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BankService bank = new BankService();
        System.out.println("==================================");
        System.out.println(" Welcome to MiniBank - Main Branch");
        System.out.println("==================================");
        boolean running = true;
        while (running) {
            System.out.println("\n1. Open Account\n2. Deposit\n3. Withdraw\n4. Transfer\n5. List Accounts\n6. Account Statement\n7. End-of-day Report\n8. Batch Deposits\n9. Exit");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> {
                        System.out.print("Owner name: "); String name = sc.nextLine().trim();
                        System.out.print("Opening balance: "); long amount = Long.parseLong(sc.nextLine().trim());
                        System.out.print("Account type (Savings/Current/Fixed Deposit): "); String type = sc.nextLine().trim();
                        double rate = switch (type.toLowerCase()) {
                            case "savings" -> 4.0; case "fixed deposit", "fixed" -> 7.0; default -> 0.0;
                        };
                        System.out.println("Opened: " + bank.openAccount(name, amount, type, rate));
                    }
                    case "2" -> {
                        System.out.print("Account number: "); String number = sc.nextLine().trim();
                        System.out.print("Amount: "); long amount = Long.parseLong(sc.nextLine().trim());
                        bank.deposit(number, amount); System.out.println("Deposit successful.");
                    }
                    case "3" -> {
                        System.out.print("Account number: "); String number = sc.nextLine().trim();
                        System.out.print("Amount: "); long amount = Long.parseLong(sc.nextLine().trim());
                        bank.withdraw(number, amount); System.out.println("Withdrawal successful.");
                    }
                    case "4" -> {
                        System.out.print("From account: "); String from = sc.nextLine().trim();
                        System.out.print("To account: "); String to = sc.nextLine().trim();
                        System.out.print("Amount: "); long amount = Long.parseLong(sc.nextLine().trim());
                        bank.transfer(from, to, amount); System.out.println("Transfer successful.");
                    }
                    case "5" -> bank.listAccounts();
                    case "6" -> {
                        System.out.print("Account number: "); System.out.println(bank.statement(sc.nextLine().trim()));
                    }
                    case "7" -> bank.report();
                    case "8" -> {
                        System.out.print("Account number: "); String number = sc.nextLine().trim();
                        System.out.print("Amount per deposit: "); long amount = Long.parseLong(sc.nextLine().trim());
                        System.out.print("Number of deposits: "); int count = Integer.parseInt(sc.nextLine().trim());
                        if (amount <= 0 || count <= 0) throw new IllegalArgumentException("Amount and count must be positive.");
                        bank.batchDeposit(number, amount, count); System.out.println("Batch completed safely.");
                    }
                    case "9" -> { bank.save(); System.out.println("Accounts saved. Thank you for using MiniBank!"); running = false; }
                    default -> System.out.println("Invalid choice. Please enter 1-9.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); System.out.println("Batch was interrupted.");
            }
        }
        sc.close();
    }
}
