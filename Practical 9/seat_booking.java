class SeatBooking 
{
    private int seatsLeft;

    public SeatBooking(int seats) 
    {
        seatsLeft = seats;
    }

    public boolean bookWithoutSync() 
    {
        if (seatsLeft > 0) 
        {
            try 
            {
                Thread.sleep(10);
            } 
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            }

            seatsLeft--;
            return true;
        }
        return false;
    }

    public synchronized boolean bookWithSync() 
    {
        if (seatsLeft > 0) 
        {
            try 
            {
                Thread.sleep(10);
            } 
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            }

            seatsLeft--;
            return true;
        }
        return false;
    }

    public int getSeatsLeft() 
    {
        return seatsLeft;
    }
}

public class seat_booking 
{
    static int successCount = 0;
    public static void main(String[] args) throws Exception 
    {
        System.out.println("=== Without Synchronization ===");

        SeatBooking booking1 = new SeatBooking(5);
        successCount = 0;

        Thread[] threads1 = new Thread[10];

        for (int i = 0; i < 10; i++) 
        {
            threads1[i] = new Thread(() -> {
                if (booking1.bookWithoutSync()) 
                {
                    System.out.println(Thread.currentThread().getName() + " booked a seat");
                    successCount++;
                }
            }, "Passenger-" + (i + 1));

            threads1[i].start();
        }

        for (Thread t : threads1) 
        {
            t.join();
        }

        System.out.println("Successful Bookings = " + successCount);
        System.out.println("Seats Left = " + booking1.getSeatsLeft());

        System.out.println("\n=== With Synchronization ===");

        SeatBooking booking2 = new SeatBooking(5);
        successCount = 0;

        Thread[] threads2 = new Thread[10];

        for (int i = 0; i < 10; i++) 
        {
            threads2[i] = new Thread(() -> {
                if (booking2.bookWithSync())
                {
                    System.out.println(Thread.currentThread().getName() + " booked a seat");
                    successCount++;
                }
            }, "Passenger-" + (i + 1));

            threads2[i].start();
        }

        for (Thread t : threads2) 
        {
            t.join();
        }

        System.out.println("Successful Bookings = " + successCount);
        System.out.println("Seats Left = " + booking2.getSeatsLeft());
    }
}