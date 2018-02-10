/***
 * Running time 1 consumer: 5681ms
 * Running time 2 consumer: 3555ms
 * Running time 3 consumer: 2849ms
 * Running time 4 consumer: 3090ms
 * Running time 5 consumer: 3097ms
***/

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class WordCount {

  private static final int NUM_COUNTERS = 5;

  public static void main(String[] args) throws Exception {
    ArrayBlockingQueue<Page> queue = new ArrayBlockingQueue<Page>(100);
    ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<String, Integer>();
    ExecutorService executor = Executors.newCachedThreadPool();

    for (int i = 0; i < NUM_COUNTERS; ++i)
      executor.execute(new Counter(queue, counts));
    Thread parser = new Thread(new Parser(queue));
    long start = System.currentTimeMillis();
    parser.start();
    parser.join();
    for (int i = 0; i < NUM_COUNTERS; ++i)
      queue.put(new PoisonPill());
    executor.shutdown();
    executor.awaitTermination(10L, TimeUnit.MINUTES);
    long end = System.currentTimeMillis();
    System.out.println("Elapsed time: " + (end - start) + "ms");

    // for (Map.Entry<String, Integer> e: counts.entrySet()) {
    //   System.out.println(e);
    // }
  }
}
