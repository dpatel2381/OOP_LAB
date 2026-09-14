public class Driver_temp 
{
    public static void main(String[] args) 
    {
        String template = "Dear {name}, order {id} ships {date}.";
        String[] names = {"name", "id"};
        String[] values = {"Riya", "A07"};

        TemplateFiller filler = new TemplateFiller(names, values);

        String result = filler.fillTemplate(template);
        System.out.println(result);
    }
}
