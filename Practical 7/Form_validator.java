import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface NotBlank {}


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface MaxLength 
{
    int value();
}

class SignupForm 
{
    @NotBlank
    @MaxLength(20)
    String username;

    @NotBlank
    @MaxLength(30)
    String email;

    @NotBlank
    @MaxLength(15)
    String password;

    SignupForm(String username, String email, String password) 
    {
        this.username=username;
        this.email=email;
        this.password=password;
    }
}

class Validator 
{

    public static List<String> validate(Object obj)
    {
        List<String> errors=new ArrayList<>();
        Class<?>classType=obj.getClass();

        for (Field field : classType.getDeclaredFields()) 
        {
            field.setAccessible(true);
            
            try
            {
                Object value=field.get(obj);

                if (field.isAnnotationPresent(NotBlank.class)) 
                {
                    if (value==null || value.toString().trim().isEmpty()) 
                    {
                        errors.add(field.getName() + " cannot be blank");
                    }
                }

                if (field.isAnnotationPresent(MaxLength.class)) 
                {
                    MaxLength annotation=field.getAnnotation(MaxLength.class);

                    int maxLength=annotation.value();

                    if (value!=null && value.toString().length()>maxLength) 
                    {
                        errors.add(field.getName() + " must have maximum " + maxLength + " characters");
                    }
                }
            }

            catch(IllegalAccessException e)
            {
                errors.add("Cannot access field: " + field.getName());
            }
        }

        return errors;
    }
}



public class Form_validator
{
    public static void main(String[] args)
    {
        SignupForm form=new SignupForm("", "dhanvi@gmail.com", "1234567890");

        List<String> errors=Validator.validate(form);

        if (errors.isEmpty())
        {
            System.out.println("Form is valid.");
        }
        else
        {
            System.out.println("Validation Errors: ");

            for (String error : errors)
            {
                System.out.println(error);
            }
        }
    }
}