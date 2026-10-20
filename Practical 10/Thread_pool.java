import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Thread_pool 
{
    public static void main(String[] args) 
    {

        ExecutorService pool = Executors.newFixedThreadPool(3);

        for (int i = 1; i <= 10; i++) 
        {
            int taskId = i;

            pool.submit(() -> {
                try 
                {
                    System.out.println("Task " + taskId +
                            " executed by " +
                            Thread.currentThread().getName());

                    Thread.sleep(1000);

                } 
                catch (InterruptedException e) 
                {
                    e.printStackTrace();
                }
            });
        }

        pool.shutdown();

        try 
        {
            pool.awaitTermination(10, TimeUnit.SECONDS);
            System.out.println("All tasks completed.");
        } 
        catch (InterruptedException e) 
        {
            e.printStackTrace();
        }
    }
}