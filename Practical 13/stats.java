public class stats {
    public static <N extends Number> double average(N[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException(
                "Array cannot be empty"
            );
        }

        double sum = 0;

        for (N value : values) {
            sum += value.doubleValue();
        }

        return sum / values.length;
    }

    public static void main(String[] args) {
        Integer[] integers = {10, 20, 30, 40};
        Double[] decimals = {2.5, 3.5, 4.5};
        Long[] longNumbers = {100L, 200L, 300L};

        System.out.println("Integer average: " + average(integers));
        System.out.println("Double average: " + average(decimals));
        System.out.println("Long average: " + average(longNumbers));
    }
}