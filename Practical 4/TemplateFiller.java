import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateFiller 
{
    private final String[] names;
    private final String[] values;

    public TemplateFiller(String[] names, String[] values) 
    {
        this.names = names;
        this.values = values;
    }

    public String fillTemplate(String template) 
    {
        Pattern p = Pattern.compile("\\{(\\w+)\\}");
        Matcher m = p.matcher(template);

        StringBuilder sb = new StringBuilder();
        int lastEnd = 0;

        while (m.find()) 
        {
            sb.append(template, lastEnd, m.start());

            String placeholder = m.group(1);
            String replacement = lookupValue(placeholder);

            sb.append(replacement);
            lastEnd = m.end();
        }

        sb.append(template.substring(lastEnd));

        return sb.toString();
    }

    private String lookupValue(String placeholder) 
    {
        for (int i = 0; i < names.length; i++) 
        {
            if (names[i].equals(placeholder)) 
            {
                return values[i];
            }
        }
        return "[?]";
    }
}
