import java.util.Random;

/**
 * NoDeadlockPhilosopher
 */
public class NoDeadlockPhilosopher extends Thread{
    private Chopstick first, second;
    private String id;
    private Random random;

    static public class Chopstick {
        Integer id = null;
        public Chopstick(Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return this.id;
        }
    };

    public NoDeadlockPhilosopher(String id, Chopstick left, Chopstick right) {
        if (left.getId() < right.getId()) {
            first = left; second = right;
        } else {
            first = right; second = left;
        }
        this.id = id;
        random = new Random();
    }

    public void run() {
        try {
            while (true) {
                // Deadlock is a danger whenever a thread tries to hold more than
                // one lock.
                // There is a simple rule that guarantees you will never
                // deadlock — always acquire locks in a fixed, global order.
                System.out.println("ID = "+this.id + " is thinking");
                Thread.sleep(random.nextInt(1000)); // Think for a while
                synchronized(first) {                // Grab first chopstick
                    System.out.println("ID = "+this.id + " has picked up left chopstick");
                    synchronized(second) {           // Grab second chopstick
                        System.out.println("ID = "+this.id + " is eating");
                        Thread.sleep(random.nextInt(1000)); // Eat for a file
                    }
                }

            }
        } catch (Exception e) {}
    }

    public static void main(String[] args) throws InterruptedException {
        NoDeadlockPhilosopher.Chopstick c1 = new NoDeadlockPhilosopher.Chopstick(1);
        NoDeadlockPhilosopher.Chopstick c2 = new NoDeadlockPhilosopher.Chopstick(2);
        NoDeadlockPhilosopher.Chopstick c3 = new NoDeadlockPhilosopher.Chopstick(3);
        NoDeadlockPhilosopher.Chopstick c4 = new NoDeadlockPhilosopher.Chopstick(4);
        NoDeadlockPhilosopher.Chopstick c5 = new NoDeadlockPhilosopher.Chopstick(5);

        NoDeadlockPhilosopher p1 = new NoDeadlockPhilosopher("1", c1, c2);
        NoDeadlockPhilosopher p2 = new NoDeadlockPhilosopher("2", c2, c3);
        NoDeadlockPhilosopher p3 = new NoDeadlockPhilosopher("3", c3, c4);
        NoDeadlockPhilosopher p4 = new NoDeadlockPhilosopher("4", c4, c5);
        NoDeadlockPhilosopher p5 = new NoDeadlockPhilosopher("5", c5, c1);

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
