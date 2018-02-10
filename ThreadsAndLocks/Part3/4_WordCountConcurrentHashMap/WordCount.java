/**
 * Running time: 604739ms
 * Even though we add another thread to help with the counting,
 * the running time is so much slower. Why?
 * The answer is excessive contention—too many threads are trying to
 * access a single shared resource simultaneously. In our case,
 * the consumers are spending so much of their time with the counts
 * map locked that they spend more time waiting for the other to
 * unlock it than they spend actually doing useful work, which leads
 * to horrid performance.
 */

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class WordCount {

  private static final int NUM_COUNTERS = 2;

  public static void main(String[] args) throws Exception {
    ArrayBlockingQueue<Page> queue = new ArrayBlockingQueue<Page>(100);
    HashMap<String, Integer> counts = new HashMap<String, Integer>();
    ExecutorService executor = Executors.newCachedThreadPool();

    for (int i = 0; i < NUM_COUNTERS; ++i)
      executor.execute(new Counter(queue, counts));
    Thread parser = new Thread(new Parser(queue));
    long start = System.currentTimeMillis();
    parser.start();

    // This will cause this thread to pause execution until
    // the parser thread has finished executing
    parser.join();

    for (int i = 0; i < NUM_COUNTERS; ++i)
      queue.put(new PoisonPill());
    executor.shutdown();
    executor.awaitTermination(10L, TimeUnit.MINUTES);
    long end = System.currentTimeMillis();

    for (Map.Entry<String, Integer> e: counts.entrySet()) {
      System.out.println(e);
    }

    System.out.println("Elapsed time: " + (end - start) + "ms");
  }
}
