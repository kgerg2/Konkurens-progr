import java.util.ArrayList;

public class Main {
    public static void testInt() {
        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {
            threads.add(new Thread(() -> {
                ThreadSafeMutableInteger n = new ThreadSafeMutableInteger();
                assert n.get() == 0;
                assert n.getAndIncrement() == 0;
                assert n.incrementAndGet() == 2;
                n.set(10);
                assert n.get() == 10;
                assert n.getAndAdd(-5) == 10;
                assert n.getAndUpdate((x) -> x + 2) == 5;
                assert n.updateAndGet((x) -> x - 5) == 3;
            }));
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

    public static void testIntArray() {
        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {
            threads.add(new Thread(() -> {
                ThreadSafeMutableIntArray array = new ThreadSafeMutableIntArray(1);
                assert array.get(0) == 0;
                array.set(0, 10);
                assert array.get(0) == 10;
                assert array.getAndUpdate(0, (x) -> x + 2) == 10;
                assert array.updateAndGet(0, (x) -> x - 5) == 7;
            }));
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

    public static void main(String[] args) {
        testIntArray();
    }
}
