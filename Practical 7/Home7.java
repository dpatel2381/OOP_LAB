import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Column 
{
    String name();
}

class Student 
{

    @Column(name="id")
    int id;

    @Column(name="name")
    String name;

    @Column(name="city")
    String city;

    @Override
    public String toString() 
    {
        return "Student{id=" + id + ", name='" + name + '\'' + ", city='" + city + '\'' + '}';
    }
}

public class Home7 
{

    public static <T> T populate(Class<T> clazz, String[] headers, String[] values) throws Exception 
    {

        T obj=clazz.getDeclaredConstructor().newInstance();

        Map<String, String> dataMap=new HashMap<>();

        for (int i=0; i<headers.length; i++) 
        {
            dataMap.put(headers[i], values[i]);
        }

        for (Field field : clazz.getDeclaredFields()) 
        {
            if (field.isAnnotationPresent(Column.class)) 
            {

                Column col=field.getAnnotation(Column.class);
                String columnName=col.name();

                field.setAccessible(true);

                if (dataMap.containsKey(columnName)) 
                {

                    String value=dataMap.get(columnName);

                    if (field.getType()==int.class) 
                    {
                        field.setInt(obj, Integer.parseInt(value));
                    }
                    else if (field.getType()==String.class) 
                    {
                        field.set(obj, value);
                    }

                } 
                else 
                {
                    System.out.println("Missing column: " + columnName + " (field left with default value)");
                }
            }
        }

        return obj;
    }

    public static void main(String[] args) throws Exception 
    {

        String[] headers={"id", "name", "city"};
        String[] values={"101", "Dhanvi", "Vadodara"};

        Student student=populate(Student.class, headers, values);

        System.out.println(student);
    }
}