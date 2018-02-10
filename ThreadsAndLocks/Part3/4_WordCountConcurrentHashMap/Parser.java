import java.util.concurrent.BlockingQueue;

/**
 * Parser
 */
public class Parser implements Runnable {
    private BlockingQueue<Page> queue;

    public Parser(BlockingQueue<Page> queue) {
        this.queue = queue;
    }

    public void run() {
        try {
            Iterable<Page> pages = new Pages(100000, "/Users/bob/Downloads/wikipedia-dump.xml");
            for (Page page : pages) {
                queue.put(page);
            }
        } catch (Exception e) {
            e.printStackTrace();;
        }
    }

}
