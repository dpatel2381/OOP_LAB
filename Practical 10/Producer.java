import java.util.LinkedList;
import java.util.Queue;

class SharedBuffer {

    private Queue<Integer> buffer = new LinkedList<>();
    private final int MAX_SIZE = 3;

    public synchronized void produce(int value) throws InterruptedException {

        while (buffer.size() == MAX_SIZE) {
            wait();
        }

        buffer.add(value);
        System.out.println("Produced: " + value);

        notify();
    }

    public synchronized int consume() throws InterruptedException {

        while (buffer.isEmpty()) {
            wait();
        }

        int value = buffer.remove();
        System.out.println("Consumed: " + value);

        notify();

        return value;
    }
}

public class Producer {

    public static void main(String[] args) {

        SharedBuffer buffer = new SharedBuffer();

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    buffer.produce(i);
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    buffer.consume();
                    Thread.sleep(800);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        producer.start();
        consumer.start();
    }
}