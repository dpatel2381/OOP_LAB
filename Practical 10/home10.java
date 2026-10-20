public class home10 { 

    private static final Object lockA = new Object();
    private static final Object lockB = new Object();

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {

            synchronized (lockA) {

                System.out.println("Thread 1 locked A");

                synchronized (lockB) {
                    System.out.println("Thread 1 locked B");
                }
            }
        });

        Thread t2 = new Thread(() -> {

            synchronized (lockA) {

                System.out.println("Thread 2 locked A");

                synchronized (lockB) {
                    System.out.println("Thread 2 locked B");
                }
            }
        });

        t1.start();
        t2.start();
    }
}