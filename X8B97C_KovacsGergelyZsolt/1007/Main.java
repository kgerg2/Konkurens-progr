public class Main {
    public static void main(String[] args) {
        Object tieBreakingLock = new Object();
        Object[] forks = new Object[5];
        Philosopher[] philosophers = new Philosopher[5];
        Thread[] threads = new Thread[5];

        for (int i = 0; i < forks.length; i++) {
            forks[i] = new Object();
        }

        for (int i = 0; i < philosophers.length; i++) {
            philosophers[i] = new Philosopher(i, forks[i], forks[(i+1) % 5], tieBreakingLock);
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(philosophers[i]);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
