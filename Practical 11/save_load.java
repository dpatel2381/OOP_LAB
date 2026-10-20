import java.io.*;

class StudentRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    int id;
    String name;
    transient String temporaryNote;

    StudentRecord(int id, String name, String temporaryNote) {
        this.id = id;
        this.name = name;
        this.temporaryNote = temporaryNote;
    }

    public String toString() {
        return id + " " + name + " Temporary note: " + temporaryNote;
    }
}

public class save_load {
    public static void main(String[] args) {
        StudentRecord[] students = {
            new StudentRecord(1, "Asha", "Temporary A"),
            new StudentRecord(2, "Rahul", "Temporary B")
        };

        try {
            ObjectOutputStream out =
                new ObjectOutputStream(new FileOutputStream("students.dat"));
            out.writeObject(students);
            out.close();

            ObjectInputStream in =
                new ObjectInputStream(new FileInputStream("students.dat"));

            StudentRecord[] loaded =
                (StudentRecord[]) in.readObject();
            in.close();

            System.out.println("Loaded students:");
            for (StudentRecord s : loaded) {
                System.out.println(s);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}