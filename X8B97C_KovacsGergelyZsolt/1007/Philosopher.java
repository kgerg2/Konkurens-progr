import java.util.Random;

public class Philosopher implements Runnable {
    private Object leftFork;
    private Object rightFork;
    private Object tieBreakingLock;
    private int id;
    private static Random random = new Random();

    public Philosopher(int id, Object leftFork, Object rightFork, Object tieBreakingLock) {
        this.id = id;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.tieBreakingLock = tieBreakingLock;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            eat();
            think();
        }
    }

    public void think() {
        System.out.println("Philosopher " + id + " is thinking...");
        try {
            Thread.sleep(random.nextInt(11));
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void eat() {
        Object first;
        Object second;

        if (System.identityHashCode(leftFork) < System.identityHashCode(rightFork)) {
            first = leftFork;
            second = rightFork;
        } else if (System.identityHashCode(leftFork) < System.identityHashCode(rightFork)) {
            first = rightFork;
            second = leftFork;
        } else {
            synchronized (tieBreakingLock) {
                eatSynchronized();
            }
            return;
        }

        synchronized (first) {
            synchronized (second) {
                eatSynchronized();
            }
        }
    }

    private void eatSynchronized() {
        System.out.println("Philosopher " + id + " is eating.");
        try {
            Thread.sleep(random.nextInt(11));
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
