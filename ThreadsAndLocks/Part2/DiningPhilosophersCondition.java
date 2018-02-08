import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * DiningPhilosophersCondition extends Thread
 */
public class DiningPhilosophersCondition extends Thread {

    private boolean eating;
    private DiningPhilosophersCondition left;
    private DiningPhilosophersCondition right;
    private ReentrantLock table;
    private Condition condition;
    private Random random;

    public DiningPhilosophersCondition(ReentrantLock table) {
        eating = false;
        this.table = table;
        condition = table.newCondition();
        random = new Random();
    }

    public void setLeft(DiningPhilosophersCondition left) { this.left = left; }
    public void setRight(DiningPhilosophersCondition right) { this.right = right; }

    public void run() {
        try {
            while (true) {
                think();
                eat();
            }
        } catch (InterruptedException e) {}
    }


    private void think() throws InterruptedException {
        table.lock();
        try {
            eating = false;
            left.condition.signal();
            right.condition.signal();
        } finally { table.unlock(); }
        Thread.sleep(1000);
    }

    private void eat() throws InterruptedException {
        table.lock();
        try {
            while (left.eating || right.eating)
                condition.wait();
            eating = true;
        } finally { table.unlock(); }
        Thread.sleep(1000);
    }
}
