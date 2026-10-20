
import java.io.*;
import java.util.ArrayList;

// Model class
class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;

    public Task(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}

// Service class
class TaskService {
    private ArrayList<Task> tasks = new ArrayList<>();

    public void addTask(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Task name cannot be empty"
            );
        }

        tasks.add(new Task(name));
    }

    public int getCount() {
        return tasks.size();
    }

    public void displayTasks() {
        for (Task task : tasks) {
            System.out.println(task);
        }
    }

    public void save(String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(filename))) {
            out.writeObject(tasks);
        }
    }

    @SuppressWarnings("unchecked")
    public void load(String filename)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(filename))) {
            tasks = (ArrayList<Task>) in.readObject();
        }
    }
}

// Main class
public class home14 {
    public static void main(String[] args) {
        String filename = "tasks.dat";

        try {
            TaskService service = new TaskService();

            service.addTask("Learn Java");
            service.addTask("Practice Generics");

            int originalCount = service.getCount();

            // Save tasks
            service.save(filename);
            System.out.println("Tasks saved successfully.");

            // Load tasks into a new service object
            TaskService loadedService = new TaskService();
            loadedService.load(filename);

            System.out.println("\nOriginal task count: " + originalCount);
            System.out.println("Loaded task count: " + loadedService.getCount());

            System.out.println("\nLoaded tasks:");
            loadedService.displayTasks();

            // Verify saved data
            if (originalCount == loadedService.getCount()) {
                System.out.println("\nSave/load test passed."
                );
            } else {
                System.out.println("\nSave/load test failed."
                );
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
