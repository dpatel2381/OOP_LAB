public class ChatFilter 
{
    private final String keyword;
    private final StringBuilder report;
    private int matches;

    public ChatFilter(String keyword) 
    {
        this.keyword = keyword.toLowerCase();
        this.report = new StringBuilder();
        this.matches = 0;
    }

    public void processLine(String line) 
    {
        String[] parts = line.split(" ", 3);
        if (parts.length < 3) 
        {
            return;
        }

        String time = parts[0];
        String user = parts[1];
        String message = parts[2];

        if (message.toLowerCase().contains(keyword))
        {
            matches++;
            report.append(time)
                  .append(" ")
                  .append(user)
                  .append(": ")
                  .append(message)
                  .append("\n");
        }
    }

    public void printReport() 
    {
        System.out.println("Matches: " + matches);
        System.out.println(report.toString());
    }
}
