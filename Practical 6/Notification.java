
@FunctionalInterface
interface Notifier 
{
    void send(String message);
}

interface Urgent {}

class EmailSender implements Notifier 
{
    @Override
    public void send(String message) 
    {
        System.out.println("Email: " + message);
    }
}

class SmsSender implements Notifier, Urgent 
{
    @Override
    public void send(String message) 
    {
        System.out.println("SMS: " + message);
    }
}

public class Notification 
{

    public static void main(String[] args) {

        String message = "Server maintenance at 10 PM";

        Notifier emailLambda = msg -> System.out.println("Email Lambda: " + msg);

        Notifier emailSender = new EmailSender();

        Notifier smsSender = new SmsSender();

        Notifier[] senders = 
        {
                emailLambda,
                emailSender,
                smsSender
        };

        for (Notifier sender : senders) 
        {
            sender.send(message);

            if (sender instanceof Urgent) 
            {
                sender.send(message);
            }
        }
    }
}