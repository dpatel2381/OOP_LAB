public class Fraction 
{
    private int num;
    private int den;

    public Fraction(int num, int den) 
    {
        if (den == 0) 
        {
            throw new IllegalArgumentException("Denominator cannot be zero");
        }
        if (den < 0) 
        {
            num = -num;
            den = -den;
        }
        int g = gcd(Math.abs(num), Math.abs(den));
        this.num = num / g;
        this.den = den / g;
    }

    private int gcd(int a, int b) 
    {
        while (b != 0) 
        {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    @Override
    public String toString() 
    {
        return num + "/" + den;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Fraction)) return false;
        Fraction f = (Fraction) o;
        return this.num == f.num && this.den == f.den;
    }

    @Override
    public int hashCode() 
    {
        return 31 * num + den;
    }
}
