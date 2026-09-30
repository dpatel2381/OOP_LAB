class MyResource implements AutoCloseable {

    public MyResource() {
        System.out.println("Resource opened");
    }

    @Override
    public void close() {
        System.out.println("Resource closed");
    }
}

public class home8  {
    public static void main(String[] args) {

        try (MyResource resource = new MyResource()) {

            System.out.println("Using resource...");

            // Exception inside try block
            throw new RuntimeException("Error inside try block");

        } catch (Exception e) {

            System.out.println("Caught: " + e.getMessage());
        }
    }
}
