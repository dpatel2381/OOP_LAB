interface Switchable {
    void on();
    void off();

    default void toggle() {
        on();
        off();
    }
}

class Fan implements Switchable {
    @Override
    public void on() {
        System.out.println("Fan ON");
    }

    @Override
    public void off() {
        System.out.println("Fan OFF");
    }

    @Override
    public String toString() {
        return "Fan";
    }
}

class Light implements Switchable {
    @Override
    public void on() {
        System.out.println("Light ON");
    }

    @Override
    public void off() {
        System.out.println("Light OFF");
    }

    @Override
    public String toString() {
        return "Light";
    }
}

@FunctionalInterface
interface SwitchPermission {
    boolean maySwitchOn(Switchable device, int hour);
}

public class Remote {
    public static void main(String[] args) {

        // Array of Switchable devices
        Switchable[] devices = {
                new Fan(),
                new Light()
        };

        System.out.println("Toggling all devices:");
        for (Switchable d : devices) {
            d.toggle();
            System.out.println();
        }

        // Anonymous class implementation
        SwitchPermission rule1 = new SwitchPermission() {
            @Override
            public boolean maySwitchOn(Switchable device, int hour) {
                return hour >= 6 && hour <= 22;
            }
        };

        // Lambda implementation
        SwitchPermission rule2 =
                (device, hour) -> device instanceof Light && hour >= 18;

        int hour = 20;

        System.out.println("Anonymous Class Rule:");
        for (Switchable d : devices) {
            System.out.println(
                    d + " allowed at " + hour + ": "
                            + rule1.maySwitchOn(d, hour));
        }

        System.out.println("\nLambda Rule:");
        for (Switchable d : devices) {
            System.out.println(
                    d + " allowed at " + hour + ": "
                            + rule2.maySwitchOn(d, hour));
        }
    }
}
