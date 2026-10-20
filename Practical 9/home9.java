import java.util.concurrent.atomic.AtomicInteger;

public class home9 
{

    static int sharedTotal = 0;
    static AtomicInteger atomicTotal = new AtomicInteger(0);

    public static synchronized void addSync(int value) 
    {
        sharedTotal += value;
    }

    public static void main(String[] args) throws InterruptedException 
    {
        int[] numbers = new int[100000];
        int expectedSum = 0;

        for (int i = 0; i < numbers.length; i++) 
        {
            numbers[i] = 1;
            expectedSum += numbers[i];
        }

        int numThreads = 4;
        int chunkSize = numbers.length / numThreads;

        sharedTotal = 0;
        Thread[] threads = new Thread[numThreads];

        long start = System.nanoTime();

        for (int t = 0; t < numThreads; t++) 
        {
            int startIndex = t * chunkSize;
            int endIndex = (t == numThreads - 1) ? numbers.length : startIndex + chunkSize;

            threads[t] = new Thread(() -> {
                for (int i = startIndex; i < endIndex; i++) 
                {
                    sharedTotal += numbers[i];
                }
            });

            threads[t].start();
        }

        for (Thread thread : threads) 
        {
            thread.join();
        }

        long end = System.nanoTime();

        System.out.println("WITHOUT Synchronization");
        System.out.println("Expected Sum = " + expectedSum);
        System.out.println("Actual Sum   = " + sharedTotal);
        System.out.println("Time = " + (end - start) + " ns\n");

        sharedTotal = 0;
        threads = new Thread[numThreads];

        start = System.nanoTime();

        for (int t = 0; t < numThreads; t++) 
        {
            int startIndex = t * chunkSize;
            int endIndex = (t == numThreads - 1) ? numbers.length : startIndex + chunkSize;

            threads[t] = new Thread(() -> {
                for (int i = startIndex; i < endIndex; i++) 
                {
                    addSync(numbers[i]);
                }
            });

            threads[t].start();
        }

        for (Thread thread : threads) 
        {
            thread.join();
        }

        end = System.nanoTime();

        System.out.println("WITH synchronized");
        System.out.println("Expected Sum = " + expectedSum);
        System.out.println("Actual Sum   = " + sharedTotal);
        System.out.println("Time = " + (end - start) + " ns\n");

        atomicTotal.set(0);
        threads = new Thread[numThreads];

        start = System.nanoTime();

        for (int t = 0; t < numThreads; t++) 
        {
            int startIndex = t * chunkSize;
            int endIndex = (t == numThreads - 1) ? numbers.length : startIndex + chunkSize;

            threads[t] = new Thread(() -> {
                for (int i = startIndex; i < endIndex; i++) 
                {
                    atomicTotal.addAndGet(numbers[i]);
                }
            });

            threads[t].start();
        }

        for (Thread thread : threads) 
        {
            thread.join();
        }

        end = System.nanoTime();

        System.out.println("WITH AtomicInteger");
        System.out.println("Expected Sum = " + expectedSum);
        System.out.println("Actual Sum   = " + atomicTotal.get());
        System.out.println("Time = " + (end - start) + " ns");
    }
}