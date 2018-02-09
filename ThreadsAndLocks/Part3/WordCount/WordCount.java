import java.awt.print.Pageable;
import java.util.HashMap;

/**
 * WordCount
 */
public class WordCount {

    private static final HashMap<String, Integer> counts =
        new HashMap<>();

    public static void main(String[] args) throws Exception {
        Iterable<Page> pages = new Pages(100000, "/Users/bob/Downloads/wikipedia-dump.xml");
        for (Page page: pages) {
            System.out.println("Looking at next page...");
            Iterable<String> words = new Words(page.getText());
            for (String word : words) {
                countWord(word);
            }
        }

        System.out.println("DONE!");
        for (String key : counts.keySet()) {
            System.out.println("Word " + key + " has count " + counts.get(key));
        }
    }

    private static void countWord(String word) {
        Integer currentCount = counts.get(word);
        if (currentCount == null)
            counts.put(word, 1);
        else
            counts.put(word, currentCount + 1);
    }
}
