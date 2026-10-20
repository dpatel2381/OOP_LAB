import java.util.NoSuchElementException;

class GenericStack<T> {
    private Object[] elements;
    private int top;

    public GenericStack(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException(
                "Capacity must be positive"
            );
        }

        elements = new Object[capacity];
        top = -1;
    }

    public void push(T value) {
        if (top == elements.length - 1) {
            throw new IllegalStateException("Stack is full");
        }

        elements[++top] = value;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }

        T value = (T) elements[top];
        elements[top] = null;
        top--;

        return value;
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public int size() {
        return top + 1;
    }
}

public class home13 {
    public static void main(String[] args) {
        GenericStack<String> names = new GenericStack<>(5);

        names.push("Asha");
        names.push("Rahul");

        System.out.println("Removed: " + names.pop());
        System.out.println("Stack size: " + names.size());

        GenericStack<Integer> numbers = new GenericStack<>(5);

        numbers.push(10);
        numbers.push(20);

        System.out.println("Removed: " + numbers.pop());

        try {
            numbers.pop();
            numbers.pop();
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
    }
}