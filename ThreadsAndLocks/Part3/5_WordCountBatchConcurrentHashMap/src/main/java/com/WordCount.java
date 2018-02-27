/**
 * Running time 1 consumer: 5296ms
 * Running time 2 consumer: 3055ms
 * Running time 3 consumer: 2590ms
 * Running time 4 consumer: 2633ms
 * Running time 4 consumer: 2796ms
 *
 * Why is the batch version faster than the parallel version??
 * Answer is because the parallel version is creating too much
 * contention for the counts map. Hence the consumers are spending
 * unnecessary time waiting.
 * The batch one is faster because each consumer stores a local
 * set of counts and merges them at the end. The merging at the
 * end means there are overall less merges in the program
 * hence the program runs faster.
 */

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
