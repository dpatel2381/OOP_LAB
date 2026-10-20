class Box<T> {
    private T value;

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}

class Pair<A, B> {
    private A first;
    private B second;

    Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public void display() {
        System.out.println("First: " + first);
        System.out.println("Second: " + second);
    }
}

public class container {
    public static <T> T first(T[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        return array[0];
    }

    public static void main(String[] args) {
        Box<String> textBox = new Box<>();
        textBox.set("Hello Java");
        System.out.println("Box value: " + textBox.get());

        Box<Integer> numberBox = new Box<>();
        numberBox.set(100);
        System.out.println("Number: " + numberBox.get());

        Pair<String, Integer> pair = new Pair<>("Age", 20);
        pair.display();

        String[] names = {"Asha", "Rahul", "Meera"};
        Integer[] numbers = {10, 20, 30};

        System.out.println("First name: " + first(names));
        System.out.println("First number: " + first(numbers));
    }
}