// Abstract base class
abstract class Media {
    String title;
    int daysLate;

    Media(String title, int daysLate) {
        this.title = title;
        this.daysLate = daysLate;
    }

    abstract double lateFee();
}

// Book: fixed fee per day
class Book extends Media {
    Book(String title, int daysLate) {
        super(title, daysLate);
    }

    @Override
    double lateFee() {
        return daysLate * 2.0; // ₹2 per day
    }
}

// DVD: higher fee, flat penalty after threshold
class DVD extends Media {
    DVD(String title, int daysLate) {
        super(title, daysLate);
    }

    @Override
    double lateFee() {
        if (daysLate <= 3) {
            return daysLate * 5.0; // ₹5 per day
        } else {
            return (3 * 5.0) + (daysLate - 3) * 10.0; // ₹10 per day after 3 days
        }
    }
}

// Magazine: small fee, capped maximum
class Magazine extends Media {
    Magazine(String title, int daysLate) {
        super(title, daysLate);
    }

    @Override
    double lateFee() {
        double fee = daysLate * 1.0; // ₹1 per day
        return Math.min(fee, 10.0);  // max ₹10
    }
}

// Driver
public class home {
    public static void main(String[] args) {
        Media[] returnedItems = {
            new Book("Java Basics", 4),
            new DVD("Inception", 5),
            new Magazine("Tech Weekly", 12),
            new Book("Data Structures", 2)
        };

        double totalFees = 0;
        for (Media m : returnedItems) {
            double fee = m.lateFee();
            System.out.println(m.title + " (Late: " + m.daysLate + " days) Fee: ₹" + fee);
            totalFees += fee;
        }
        System.out.println("Total Late Fees: ₹" + totalFees);
    }
}
