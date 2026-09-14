public class ShapeAreas 
{
    public abstract class Shape 
    {
        public abstract double area();
    }

    public class Circle extends Shape 
    {
        double radius;

        public Circle(double radius) 
        {
            this.radius = radius;
        }

        @Override
        public double area() 
        {
            final double PI = 3.14;
            return PI*radius*radius;
        }
    }

    public class Rectangle extends Shape 
    {
        double length;
        double width;

        public Rectangle(double length, double width) 
        {
            this.length=length;
            this.width=width;
        }

        @Override
        public double area() 
        {
            return length*width;
        }
    }

    public class triangle extends Shape 
    {
        double base;
        double height;

        public triangle(double base, double height) 
        {
            this.base=base;
            this.height=height;
        }

        @Override
        public double area() 
        {
            return 0.5*base*height;
        }
    }

    public static void main(String[] args) 
    {
        ShapeAreas sa=new ShapeAreas();
        Shape[] s={sa.new Circle(5), sa.new Rectangle(4, 6), sa.new triangle(3, 8)};

        double totalArea=0;
        double largest=0;
        for(int i=0; i<s.length; i++)
        {
            System.out.println("Area of shape "+(i+1)+" is: "+s[i].area());
            totalArea+=s[i].area();

            if(s[i].area() > largest)
            {
                largest = s[i].area();
            }
        }

        System.out.println("Total area of all shapes is: "+ totalArea);
        System.out.println("Maximum area among all shapes is: "+ largest);
    }
    
}
