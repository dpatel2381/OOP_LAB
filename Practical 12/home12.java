import java.util.*;

public class home12 {
    public static void main(String[] args) {
        Map<String, Integer> scores = new HashMap<>();

        scores.put("Asha", 95);
        scores.put("Rahul", 88);
        scores.put("Meera", 95);
        scores.put("Dev", 78);
        scores.put("Kiran", 88);

        int topN = 3;

        List<Map.Entry<String, Integer>> leaderboard =
            new ArrayList<>(scores.entrySet());

        leaderboard.sort(
            Comparator.<Map.Entry<String, Integer>>
                comparingInt(Map.Entry::getValue)
                .reversed()
                .thenComparing(Map.Entry::getKey)
        );

        System.out.println("Top " + topN + " players:");
        for (int i = 0; i < Math.min(topN, leaderboard.size()); i++) {
            Map.Entry<String, Integer> player = leaderboard.get(i);
            System.out.println(player.getKey() + " - " + player.getValue());
        }
    }
}