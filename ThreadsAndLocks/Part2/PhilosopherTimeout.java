import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;
import java.util.Random;

/**
 * Philosopher
 */
public class PhilosopherTimeout extends Thread{
    private ReentrantLock leftChopstick, rightChopstick;
    private String id;
    private Random random;

    public PhilosopherTimeout(String id, ReentrantLock left, ReentrantLock right) {
        this.id = id;
        this.leftChopstick = left;
        this.rightChopstick = right;
        random = new Random();
    }

    public void run() {
        try {
            while (true) {
                System.out.println("Philosopher " + id + " is thinking");
                Thread.sleep(random.nextInt(1000));
                leftChopstick.lock();
                try {
                    System.out.println("Philosopher " + id + " got the left chopstick");
                    // Instead of using lock(), this code uses tryLock(), which times out if it fails to acquire the lock.
                    // This means that, even though we don’t follow the “acquire multiple locks in a fixed, global order”
                    // rule, this version will not deadlock (or at least, will not deadlock forever).
                    if (rightChopstick.tryLock(1000, TimeUnit.MILLISECONDS)) {
                        try {
                            System.out.println("Philosopher " + id + " got the right chopstick");
                            Thread.sleep(random.nextInt(1000));
                        } finally { rightChopstick.unlock(); }
                    } else {
                        System.out.println("Philosopher " + id + " Didn't get right chopstick - give up and go back to thinking");
                    }
                } finally { leftChopstick.unlock(); }
            }
        } catch (InterruptedException e) {}
    }

    public static void main(String[] args) throws InterruptedException {
        ReentrantLock c1 = new ReentrantLock();
        ReentrantLock c2 = new ReentrantLock();
        ReentrantLock c3 = new ReentrantLock();
        ReentrantLock c4 = new ReentrantLock();
        ReentrantLock c5 = new ReentrantLock();

        Philosopher p1 = new Philosopher("1", c1, c2);
        Philosopher p2 = new Philosopher("2", c2, c3);
        Philosopher p3 = new Philosopher("3", c3, c4);
        Philosopher p4 = new Philosopher("4", c4, c5);
        Philosopher p5 = new Philosopher("5", c5, c1);

        p1.start();
        p2.start();
        p3.start();
        p4.start();
        p5.start();

        p2.join();
        p3.join();
        p3.join();
        p4.join();
        p5.join();
    }
}
