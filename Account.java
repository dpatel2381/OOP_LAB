public class Account 
{
    private final String accountNumber;
    private String ownerName;
    private long balance;
    private boolean active;

    private static int accountCounter = 1;

    public Account(String ownerName, long openingBalance) 
    {
        this.ownerName = ownerName;
        this.balance = openingBalance;
        this.active = true;
        this.accountNumber = generateAccountNumber();
    }

    public Account(String ownerName) 
    {
        this(ownerName, 0);
    }

    private static String generateAccountNumber() 
    {
        return String.format("AC%04d", accountCounter++);
    }


    public void deposit(long amount) 
    {
        if (amount > 0) 
        {
            balance += amount;
        }
    }

    public boolean withdraw(long amount) 
    {
        if (amount > 0 && balance >= amount) 
        {
            balance -= amount;
            return true;
        }
        return false;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public long getBalance() { return balance; }
    public boolean isActive() { return active; }
}
