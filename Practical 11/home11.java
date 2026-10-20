import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;

public class home11 {
    public static void main(String[] args) throws IOException {
        Path folder = Path.of(".");
        Path report = Path.of("folder-report.txt");

        try (var writer = Files.newBufferedWriter(report)) {
            Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(
                        Path file, BasicFileAttributes attrs)
                        throws IOException {

                    String details =
                        "Name: " + file.getFileName()
                        + ", Size: " + attrs.size() + " bytes"
                        + ", Modified: " + attrs.lastModifiedTime();

                    writer.write(details);
                    writer.newLine();
                    System.out.println(details);

                    return FileVisitResult.CONTINUE;
                }
            });
        }

        System.out.println("Report saved to " + report);
    }
}