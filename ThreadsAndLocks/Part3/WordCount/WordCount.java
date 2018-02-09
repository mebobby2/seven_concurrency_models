import java.util.Map;
import java.awt.print.Pageable;
import java.util.HashMap;

/**
 * WordCount
 * Running time: 6459ms
 */
public class WordCount {

    private static final HashMap<String, Integer> counts =
        new HashMap<>();

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        Iterable<Page> pages = new Pages(100000, "/Users/bob/Downloads/wikipedia-dump.xml");
        for (Page page: pages) {
            Iterable<String> words = new Words(page.getText());
            for (String word : words) {
                countWord(word);
            }
        }
        long end = System.currentTimeMillis();

        for (Map.Entry<String, Integer> e: counts.entrySet()) {
            System.out.println(e);
        }

        System.out.println("Elapsed time: " + (end - start) + "ms");
    }

    private static void countWord(String word) {
        Integer currentCount = counts.get(word);
        if (currentCount == null)
            counts.put(word, 1);
        else
            counts.put(word, currentCount + 1);
    }
}
