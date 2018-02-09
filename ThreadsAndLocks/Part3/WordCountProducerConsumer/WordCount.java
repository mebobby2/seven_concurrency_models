import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * WordCount
 * Running time: 5060ms
 */
public class WordCount {

  public static void main(String[] args) throws Exception {
    ArrayBlockingQueue<Page> queue = new ArrayBlockingQueue<Page>(100);
    HashMap<String, Integer> counts = new HashMap<String, Integer>();

    Thread counter = new Thread(new Counter(queue, counts));
    Thread parser = new Thread(new Parser(queue));
    long start = System.currentTimeMillis();
    counter.start();
    parser.start();
    parser.join();
    queue.put(new PoisonPill());
    counter.join();
    long end = System.currentTimeMillis();

    for (Map.Entry<String, Integer> e: counts.entrySet()) {
        System.out.println(e);
    }

    System.out.println("Elapsed time: " + (end - start) + "ms");
  }
}
