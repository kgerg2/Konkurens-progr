import java.util.ArrayList;
import java.util.List;

public class Task1 {
    private static final int NUM_THREADS = 10;

    public static List<String> generate(final int from, final int to, final int count) {
        if (from < 0 || to < 0 || !isInRange(count, 0, to - from + 1))
            throw new IllegalArgumentException();

        List<String> generated = new ArrayList<>(count);

        // TODO Create `NUM_THREADS` threads.
        // TODO Each thread:
        // - generates a random number in the [from, to] interval
        // - converts it into kanji using `KanjiLib.convert`
        // - creates a string "<number>, <kanji>" using the given input and converted
        // string
        // - if `generated` has size equal to `count`, it exits immediately
        // - puts the string into `generated` if it is not already present

        Thread[] threads = new Thread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new Thread(() -> {
                while (true) {
                    int n = from + (int) (Math.random() * (to - from + 1));

                    String res = String.format("%d, %s", n, KanjiLib.convert(n));

                    synchronized (generated) {
                        if (generated.size() >= count)
                            break;

                        if (!generated.contains(res)) {
                            generated.add(res);
                        }
                    }
                }
            });
        }

        // TODO Start the above threads.

        for (Thread thread : threads) {
            thread.start();
        }

        // TODO Wait for each thread to finish.

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return generated;
    }

    private static boolean isInRange(int count, int from, int to) {
        return from <= count && count <= to;
    }
}
