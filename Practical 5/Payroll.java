// Employee.java
abstract class Employee {
    String name;
    int id;

    Employee(String name, int id) {
        this.name = name;
        this.id = id;
    }

    abstract double monthlySalary();
}

// FullTime.java
class FullTime extends Employee {
    double fixedSalary;

    FullTime(String name, int id, double fixedSalary) {
        super(name, id);
        this.fixedSalary = fixedSalary;
    }

    @Override
    double monthlySalary() {
        return fixedSalary;
    }
}

// PartTime.java
class PartTime extends Employee {
    int hours;
    double rate;

    PartTime(String name, int id, int hours, double rate) {
        super(name, id);
        this.hours = hours;
        this.rate = rate;
    }

    @Override
    double monthlySalary() {
        return hours * rate;
    }
}

// Intern.java
class Intern extends Employee {
    double stipend;

    Intern(String name, int id, double stipend) {
        super(name, id);
        this.stipend = stipend;
    }

    @Override
    double monthlySalary() {
        return stipend;
    }
}

// Driver.java
public class Payroll {
    public static void main(String[] args) {
        Employee[] employees = {
            new FullTime("Alice", 101, 50000),
            new PartTime("Bob", 102, 80, 200),
            new Intern("Charlie", 103, 10000),
            new PartTime("Dhanvi", 104, 60, 150)
        };

        double total = 0;
        for (Employee e : employees) {
            double salary = e.monthlySalary();
            System.out.print(e.name + " (ID: " + e.id + ") Salary: " + salary);
            if (e instanceof Intern) {
                System.out.print(" [Intern]");
            }
            System.out.println();
            total += salary;
        }
        System.out.println("Total Payroll: " + total);
    }
}
