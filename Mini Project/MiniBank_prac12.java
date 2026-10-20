import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/* Practical 12: Collections Framework, sorting and thread-safe shared account storage. */
public class MiniBank_prac12 {
    static class Account implements Comparable<Account> {
        private final String accountNumber;
        private final String ownerName;
        private long balance;
        Account(String accountNumber, String ownerName, long balance) {
            this.accountNumber = accountNumber; this.ownerName = ownerName; this.balance = balance;
        }
        public String getAccountNumber() { return accountNumber; }
        public String getOwnerName() { return ownerName; }
        public synchronized long getBalance() { return balance; }
        public synchronized void deposit(long amount) {
            if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
            balance += amount;
        }
        public synchronized boolean withdraw(long amount) {
            if (amount <= 0 || amount > balance) return false;
            balance -= amount; return true;
        }
        @Override public int compareTo(Account other) {
            return accountNumber.compareTo(other.accountNumber);
        }
        @Override public String toString() {
            return accountNumber + " | " + ownerName + " | Balance: ₹" + getBalance();
        }
    }

    static class AccountStore {
        private final HashMap<String, Account> accounts = new HashMap<>();
        private final ArrayList<String> transactionHistory = new ArrayList<>();
        public void add(Account account) {
            accounts.put(account.getAccountNumber(), account);
        }
        public Account get(String accountNumber) { return accounts.get(accountNumber); }
        public ArrayList<Account> getAll() { return new ArrayList<>(accounts.values()); }
        public void addHistory(String entry) { transactionHistory.add(entry); }
        public ArrayList<String> getTransactionHistory() { return new ArrayList<>(transactionHistory); }
        public ArrayList<Account> sortedByBalance() {
            ArrayList<Account> result = getAll();
            result.sort(Comparator.comparingLong(Account::getBalance).reversed());
            return result;
        }
        public ArrayList<Account> aboveBalance(long minimum) {
            ArrayList<Account> result = new ArrayList<>();
            for (Account account : accounts.values())
                if (account.getBalance() > minimum) result.add(account);
            result.sort(Comparator.comparingLong(Account::getBalance).reversed());
            return result;
        }
    }

    static class TransactionProcessor {
        private final ConcurrentHashMap<String, Account> sharedAccounts = new ConcurrentHashMap<>();
        public void add(Account account) { sharedAccounts.put(account.getAccountNumber(), account); }
        public Account get(String number) { return sharedAccounts.get(number); }
        public Collection<Account> getAll() { return new ArrayList<>(sharedAccounts.values()); }
        public boolean deposit(String number, long amount) {
            Account account = sharedAccounts.get(number);
            if (account == null || amount <= 0) return false;
            account.deposit(amount); return true;
        }
        public boolean withdraw(String number, long amount) {
            Account account = sharedAccounts.get(number);
            return account != null && account.withdraw(amount);
        }
    }

    public static void main(String[] args) {
        AccountStore store = new AccountStore();
        Account a1 = new Account("AC0001", "Dhanvi", 5000);
        Account a2 = new Account("AC0002", "Maya", 2000);
        Account a3 = new Account("AC0003", "Prit", 9000);
        store.add(a1); store.add(a2); store.add(a3);
        store.addHistory("DEPOSIT | AC0001 | 500");
        store.addHistory("WITHDRAW | AC0002 | 100");

        System.out.println("=== MiniBank Practical 12 ===");
        System.out.println("Lookup AC0002: " + store.get("AC0002"));
        System.out.println("\nAccounts sorted by balance (highest first):");
        for (Account account : store.sortedByBalance()) System.out.println(account);
        System.out.println("\nTransaction history:");
        for (String item : store.getTransactionHistory()) System.out.println(item);

        TransactionProcessor processor = new TransactionProcessor();
        for (Account account : store.getAll()) processor.add(account);
        processor.deposit("AC0001", 500);
        System.out.println("\nThread-safe store after deposit:");
        for (Account account : processor.getAll()) System.out.println(account);
    }
}
