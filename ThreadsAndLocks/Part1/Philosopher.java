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
        Philosopher.Chopstick left = new Philosopher.Chopstick();
        Philosopher.Chopstick right = new Philosopher.Chopstick();

        Philosopher p1 = new Philosopher("1", left, right);
        Philosopher p2 = new Philosopher("2", left, right);
        Philosopher p3 = new Philosopher("3", left, right);
        Philosopher p4 = new Philosopher("4", left, right);
        Philosopher p5 = new Philosopher("5", left, right);

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
