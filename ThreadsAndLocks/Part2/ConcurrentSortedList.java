import java.util.concurrent.locks.ReentrantLock;

/**
 * ConcurrentSortedList
 */
public class ConcurrentSortedList {

    private class Node {
        int value;
        Node prev;
        Node next;
        ReentrantLock lock = new ReentrantLock();

        Node() {}

        Node(int value, Node prev, Node next) {
            this.value = value; this.prev = prev; this.next = next;
        }
    }

    private final Node head;
    private final Node tail;

    public ConcurrentSortedList() {
        head = new Node(); tail = new Node();
        head.next = tail; tail.prev = head;
    }

    public void insert(int value) {
        Node current = head;
        current.lock.lock();
        Node next = current.next;
        try {
            while(true) {
                next.lock.lock();
                try {
                    if (next == tail || next.value < value) {
                        Node node = new Node(value, current, next);
                        next.prev = node;
                        current.next = node;
                        return;
                    }
                } finally { current.lock.unlock(); }
                current = next;
                next = current.next;
            }
        } finally { next.lock.unlock(); }
    }

    public int size() {
        Node current = tail;
        int count = 0;

        while (current.prev != head) {
            ReentrantLock lock = current.lock;
            lock.lock();
            try {
                ++count;
                current = current.prev;
            } finally { lock.unlock(); }
        }

        return count;
    }

    public static void main(String[] args) throws InterruptedException {
        ConcurrentSortedList list = new ConcurrentSortedList();

        Thread t1 = new Thread() {
            public void run() {
                list.insert(1);
                list.insert(4);
                list.insert(6);
                list.insert(8);
                list.insert(10);
            }
        };

        Thread t2 = new Thread() {
            public void run() {
                list.insert(2);
                list.insert(5);
                list.insert(3);
                list.insert(9);
                list.insert(7);
            }
        };

        Thread t3 = new Thread() {
            public void run() {
                list.insert(15);
                list.insert(12);
                list.insert(13);
                list.insert(11);
                list.insert(12);
            }
        };

        t1.start(); t2.start(); t3.start();
        t1.join(); t2.join(); t3.join();

        System.out.println("The length of the list is " + list.size());

        Node current = list.head;
        while (current != list.tail) {
            current = current.next;
            System.out.println(current.value);
        }
    }
}
