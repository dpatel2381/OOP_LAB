import java.util.*;

public class word {
    public static void main(String[] args) {
        String paragraph =
            "java is easy and java is powerful";

        Map<String, Integer> frequency = new HashMap<>();

        for (String word : paragraph.toLowerCase().split("\\W+")) {
            if (!word.isEmpty()) {
                frequency.put(word,
                    frequency.getOrDefault(word, 0) + 1);
            }
        }

        List<Map.Entry<String, Integer>> entries =
            new ArrayList<>(frequency.entrySet());

        entries.sort(
            Comparator.<Map.Entry<String, Integer>>
                comparingInt(Map.Entry::getValue)
                .reversed()
                .thenComparing(Map.Entry::getKey)
        );

        for (Map.Entry<String, Integer> entry : entries) {
            System.out.println(entry.getKey() + " : "
                               + entry.getValue());
        }
    }
}