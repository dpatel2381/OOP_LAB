import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;
import java.util.List;

public class log_analyzer {
    public static void main(String[] args) throws IOException {
        Path file1 = Path.of("app1.txt");
        Path file2 = Path.of("app2.txt");

        Files.write(file1, List.of(
            "Application started",
            "ERROR: Connection failed",
            "Application stopped"
        ));

        Files.write(file2, List.of(
            "User logged in",
            "ERROR: File missing"
        ));

        Path[] files = {file1, file2};
        int totalLines = 0;
        int keywordMatches = 0;
        String keyword = "ERROR";

        for (Path file : files) {
            List<String> lines = Files.readAllLines(file);

            for (String line : lines) {
                totalLines++;
                if (line.contains(keyword)) {
                    keywordMatches++;
                }
            }

            BasicFileAttributes attributes =
                Files.readAttributes(file, BasicFileAttributes.class);

            System.out.println("File: " + file.getFileName());
            System.out.println("Size: " + attributes.size() + " bytes");
        }

        System.out.println("Total lines: " + totalLines);
        System.out.println("Lines containing " + keyword + ": "
                           + keywordMatches);
    }
}