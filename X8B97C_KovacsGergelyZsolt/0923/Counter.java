public class Counter {
    private int counter = 0;

    // public synchronized void increment() {
    public void increment() {
        counter++;
    }

    // public synchronized int get() {
    public int get() {
        return counter;
    }
}

class Main {
    public static void main(String[] args) {
        Counter c = new Counter();
        Thread[] threads = new Thread[1000];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(c::increment);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(c.get());
    }
}
