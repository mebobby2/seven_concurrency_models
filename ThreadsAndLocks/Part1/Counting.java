public class Counting {
    public static void main(String[] args) throws InterruptedException {
        class Counter {
            private int count = 0;
            public synchronized void increment() { ++count; }
            public int getCount() { return count; }
        }
        final Counter counter = new Counter();
        class CountingThread extends Thread {
            public void run() {
                for(int x = 0; x < 10000; ++x) {
                    counter.increment();
                }
            }
        }

        CountingThread t1 = new CountingThread();
        CountingThread t2 = new CountingThread();
        t1.start(); t2.start();
        t1.join(); t2.join();

        // Making increment() synchronized isn’t enough — getCount() needs to be synchronized as well. If it isn’t, a
        // thread calling getCount() may see a stale value (as it happens, the way that getCount() is used in this
        // code is thread-safe, because it’s called after a call to join(), but it’s a ticking time bomb waiting for
        // anyone who uses Counter).
        System.out.println(counter.getCount());
    }
}
