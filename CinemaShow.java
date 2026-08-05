public class CinemaShow
{
    private String title;
    private int seatsAvailable;
    private final int capacity;
    private static int totalBooked=0;

    CinemaShow(String title, int capacity)
    {
        this.title=title;
        this.capacity=capacity;
        this.seatsAvailable=capacity;
    }

    CinemaShow(String title)
    {
        this(title,100);
    }

    boolean book(int n)
    {
        if(n<=seatsAvailable && totalBooked<capacity)
        {
            this.seatsAvailable-=n;
            totalBooked+=n;
            System.out.println("number of seats booked:"+n);
            System.out.println("succesfully booked!");
            return true;
        }
        else
        {
            System.out.println("Error!");
            System.out.println("All seats are full!");
            return false;
        }
    }

    void cancel(int n)
    {
        if(n<=capacity-seatsAvailable)
        {
            this.seatsAvailable+=n;
            totalBooked-=n;
            System.out.println("number of seats canceled:"+n);
            System.out.println("Successful canceled.");
        }
    }

    int getSeatsAvailable()
    {
        return this.seatsAvailable;
    }

    static int getTotalBooked()
    {
        return totalBooked;
    }

    public static void main(String[] args)
    {
        CinemaShow c1= new CinemaShow("dhamal 4",50);
        c1.book(5);
        c1.cancel(3);
        c1.book(7);
        c1.book(2);
        c1.book(6);
        c1.cancel(9);
        c1.cancel(10);
        c1.book(16);
        c1.book(21);
        c1.cancel(5);
        c1.book(11);

        System.out.println("totol number of seats booked:"+ c1.getTotalBooked());
    }

}
