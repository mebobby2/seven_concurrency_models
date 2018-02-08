import java.util.Random;

/**
 * Philosopher
 */
public class Philosopher extends Thread{
    private Chopstick left, right;
    private String id;
    private Random random;

    static public class Chopstick {};

    public Philosopher(String id, Chopstick left, Chopstick right) {
        this.id = id;
        this.left = left; this.right = right;
        random = new Random();
    }

    public void run() {
        try {
            while (true) {
                // To increase the changes of a deadlock, decrease the philosophers
                // eating time (since if at least one philosopher is eating, no deadlock can happen
                // so we want to decrease the time when the philosopher is in the eating state) and
                // add a sleep of 1 second after the philosopher has picked up the first
                // chopstick but before picking up the second chopstick.
                System.out.println("ID = "+this.id + " is thinking");
                Thread.sleep(random.nextInt(1000)); // Think for a while
                synchronized(left) {                // Grab left chopstick
                    System.out.println("ID = "+this.id + " has picked up left chopstick");
                    synchronized(right) {           // Grab right chopstick
                        System.out.println("ID = "+this.id + " is eating");
                        Thread.sleep(random.nextInt(1000)); // Eat for a file
                    }
                }

            }
        } catch (Exception e) {}
    }

    public static void main(String[] args) throws InterruptedException {
        Philosopher.Chopstick c1 = new Philosopher.Chopstick();
        Philosopher.Chopstick c2 = new Philosopher.Chopstick();
        Philosopher.Chopstick c3 = new Philosopher.Chopstick();
        Philosopher.Chopstick c4 = new Philosopher.Chopstick();
        Philosopher.Chopstick c5 = new Philosopher.Chopstick();

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
