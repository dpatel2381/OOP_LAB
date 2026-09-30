import java.util.HashMap;

class OutOfStockException extends Exception 
{
    private int shortfall;

    public OutOfStockException(String message, int shortfall) 
    {
        super(message);
        this.shortfall = shortfall;
    }

    public int getShortfall() 
    {
        return shortfall;
    }
}

class InvalidQuantityException extends Exception 
{
    public InvalidQuantityException(String message) 
    {
        super(message);
    }
}

class Warehouse 
{
    private HashMap<String, Integer> stock = new HashMap<>();

    public Warehouse() 
    {
        stock.put("Laptop", 10);
        stock.put("Mouse", 20);
        stock.put("Keyboard", 5);
    }

    public void issue(String item, int qty) throws OutOfStockException, InvalidQuantityException 
    {
        if (qty <= 0) 
        {
            throw new InvalidQuantityException("Quantity must be greater than 0.");
        }

        int available = stock.getOrDefault(item, 0);

        if (qty > available) 
        {
            int shortfall = qty - available;
            throw new OutOfStockException(item + " is out of stock.", shortfall);
        }

        stock.put(item, available - qty);
        System.out.println("Issued " + qty + " " + item + "(s). Remaining stock: " + (available - qty));
    }
}

public class Stock
{
    public static void main(String[] args) 
    {
        Warehouse warehouse = new Warehouse();

        String[] items = {"Laptop", "Mouse", "Keyboard", "Laptop"};
        int[] quantities = {5, 25, -2, 3};

        for (int i = 0; i < items.length; i++) 
        {
            try 
            {
                warehouse.issue(items[i], quantities[i]);
            } 
            catch (OutOfStockException e) 
            {
                System.out.println("Stock Error: " + e.getMessage() + " Shortfall = " + e.getShortfall());

            } 
            catch (InvalidQuantityException e) 
            {
                System.out.println("Quantity Error: " + e.getMessage());
            }
        }

        System.out.println("\nAll requests processed.");
    }
}