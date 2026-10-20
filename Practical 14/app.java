import java.util.ArrayList;
import java.util.Scanner;

class Task {
    private String name;

    public Task(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class TaskService_ {
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

    public void listTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks available.");
            return;
        }

        for (Task task : tasks) {
            System.out.println("- " + task.getName());
        }
    }

    public void performAction(String action, String name) {
        if ("add".equalsIgnoreCase(action)) {
            addTask(name);
        } else if ("list".equalsIgnoreCase(action)) {
            listTasks();
        } else {
            throw new IllegalArgumentException("Invalid action");
        }
    }
}

public class app {
    public static void testAddIncreasesCount() {
        TaskService_ service = new TaskService_();
        service.addTask("Study Java");

        if (service.getCount() != 1) {
            throw new AssertionError("Count test failed");
        }

        System.out.println("Add count test passed");
    }

    public static void testInvalidActionThrows() {
        TaskService_ service = new TaskService_();

        try {
            service.performAction("delete", "");
            throw new AssertionError("Expected exception was not thrown");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid action test passed");
        }
    }

    public static void runTests() {
        testAddIncreasesCount();
        testInvalidActionThrows();
    }

    public static void main(String[] args) {
        TaskService_ service = new TaskService_();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Add task");
            System.out.println("2. List tasks");
            System.out.println("3. Show task count");
            System.out.println("4. Run demonstration tests");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
                continue;
            }

            try {
                switch (choice) {
                    case 1:
                        System.out.print("Enter task name: ");
                        service.addTask(sc.nextLine());
                        System.out.println("Task added.");
                        break;

                    case 2:
                        service.listTasks();
                        break;

                    case 3:
                        System.out.println(
                            "Task count: " + service.getCount()
                        );
                        break;

                    case 4:
                        runTests();
                        break;

                    case 5:
                        sc.close();
                        return;

                    default:
                        System.out.println("Invalid menu choice.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}