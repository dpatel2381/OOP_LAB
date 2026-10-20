import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.*;

public class MiniBank_prac11 {
    static class Account implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String accountNumber;
        private final String holderName;
        private long balance;
        private final String accountType;
        private final double interestRate;

        Account(String accountNumber, String holderName, long balance,
                String accountType, double interestRate) {
            this.accountNumber = accountNumber;
            this.holderName = holderName;
            this.balance = balance;
            this.accountType = accountType;
            this.interestRate = interestRate;
        }
        public String getAccountNumber() { return accountNumber; }
        public String getHolderName() { return holderName; }
        public long getBalance() { return balance; }
        public String getAccountType() { return accountType; }
        public double interestRate() { return interestRate; }
        public synchronized void deposit(long amount) {
            if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
            balance += amount;
        }
        public synchronized boolean withdraw(long amount) {
            if (amount <= 0 || amount > balance) return false;
            balance -= amount;
            return true;
        }
        @Override public String toString() {
            return accountNumber + " | " + holderName + " | " + accountType
                    + " | Balance: ₹" + balance + " | Interest: " + interestRate + "%";
        }
    }

    static class Customer implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String customerId, name, email, mobile;
        Customer(String customerId, String name, String email, String mobile) {
            this.customerId = customerId; this.name = name; this.email = email; this.mobile = mobile;
        }
        @Override public String toString() {
            return customerId + " | " + name + " | " + email + " | " + mobile;
        }
    }

    static class StatePersister {
        public static void save(Account[] accounts, Path file) throws IOException {
            Path parent = file.toAbsolutePath().getParent();
            if (parent != null) Files.createDirectories(parent);
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
                out.writeObject(accounts);
            }
        }
        public static Account[] load(Path file) throws IOException, ClassNotFoundException {
            if (!Files.exists(file)) return new Account[0];
            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
                return (Account[]) in.readObject();
            }
        }
    }

    static class TransactionLog {
        public static void append(Path file, String line) throws IOException {
            Path parent = file.toAbsolutePath().getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.write(file, (line + System.lineSeparator()).getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

    static class ReportGenerator {
        public static String generate(Path logsFolder, Path reportFile) throws IOException {
            Files.createDirectories(logsFolder);
            long deposits = 0, withdrawals = 0;
            StringBuilder report = new StringBuilder("=== MiniBank End-of-Day Report ===\n");
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsFolder, "*.log")) {
                for (Path file : stream) {
                    BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
                    report.append("\nFile: ").append(file.getFileName())
                          .append(" | Size: ").append(attr.size())
                          .append(" bytes | Modified: ").append(attr.lastModifiedTime()).append('\n');
                    try (BufferedReader reader = Files.newBufferedReader(file)) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            report.append(line).append('\n');
                            String[] p = line.split("\\|", -1);
                            if (p.length >= 3) {
                                try {
                                    long amount = Long.parseLong(p[2].trim());
                                    if (p[1].trim().equalsIgnoreCase("DEPOSIT")) deposits += amount;
                                    if (p[1].trim().equalsIgnoreCase("WITHDRAW")) withdrawals += amount;
                                } catch (NumberFormatException ignored) { }
                            }
                        }
                    }
                }
            }
            report.append("\nTotal deposits: ₹").append(deposits)
                  .append("\nTotal withdrawals: ₹").append(withdrawals).append('\n');
            Path parent = reportFile.toAbsolutePath().getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.write(reportFile, report.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return report.toString();
        }
    }

    public static void main(String[] args) {
        Path dataFile = Paths.get("data", "accounts-prac11.dat");
        Path logsFolder = Paths.get("logs");
        Path logFile = logsFolder.resolve("transactions.log");
        Path reportFile = Paths.get("reports", "end-of-day-report.txt");
        try {
            Account[] accounts = StatePersister.load(dataFile);
            if (accounts.length == 0) {
                accounts = new Account[] {
                    new Account("ACC1001", "Dhanvi", 15000, "Savings", 4.0),
                    new Account("ACC1002", "Maya", 7000, "Current", 0.0),
                    new Account("ACC1003", "Prit", 10000, "Fixed Deposit", 7.0)
                };
            }
            System.out.println("=== MiniBank Practical 11 ===");
            for (Account account : accounts) System.out.println(account);

            accounts[0].deposit(1000);
            TransactionLog.append(logFile, LocalDateTime.now() + " | DEPOSIT | 1000 | " + accounts[0].getAccountNumber());
            if (accounts[1].withdraw(500)) {
                TransactionLog.append(logFile, LocalDateTime.now() + " | WITHDRAW | 500 | " + accounts[1].getAccountNumber());
            }
            StatePersister.save(accounts, dataFile);
            System.out.println("\nSaved and reloaded accounts:");
            for (Account account : StatePersister.load(dataFile)) System.out.println(account);
            System.out.println("\n" + ReportGenerator.generate(logsFolder, reportFile));
            System.out.println("Report saved to: " + reportFile);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("MiniBank file operation failed: " + e.getMessage());
        }
    }
}
