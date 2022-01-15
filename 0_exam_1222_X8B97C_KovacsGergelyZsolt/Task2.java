import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Task2 {
    private static final int NUM_THREADS = 10;
    private static final int CHANNEL_CAPACITY = 100;
    private static final int POISON_PILL = -1;

    public static List<String> generate(final int from, final int to, final int count) {
        if (from < 0 || to < 0 || !isInRange(count, 0, to - from + 1))
            throw new IllegalArgumentException();

        List<String> generated = new ArrayList<>(count);

        // TODO Define a data structure that will be used as a bounded communication
        // channel between threads
        // the maximal capacity of the channel must be `CHANNEL_CAPACITY`.

        BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(CHANNEL_CAPACITY);

        // TODO Create a producer thread (A) that generates `count` random numbers on
        // the
        // [from, to] interval and sends them to consumers (B) using a bounded channel.
        // TODO Random numbers must be unique (use a thread-confined data structure to
        // keep track).
        // TODO This is thread cannot be interrupted.
        // TODO When random number generation ends signal end of transmission to each
        // other thread (B)
        // using the `POISON_PILL` value.

        Thread A = new Thread(() -> {
            Set<Integer> set = new HashSet<Integer>();
            while (set.size() < count) {
                int n = from + (int) (Math.random() * (to - from + 1));
                if (set.add(n)) {
                    try {
                        queue.put(n);
                    } catch (InterruptedException e) {
                        // ignored
                    }
                }
            }
            for (int i = 0; i < NUM_THREADS; i++) {
                try {
                    queue.put(POISON_PILL);
                } catch (InterruptedException e) {
                    // ignored
                }
            }
        });

        // TODO Create `NUM_THREADS` threads. Each thread:
        // - receives a number from thread A
        // - if the received number equals `POISON_PILL`, it exits immediately
        // - converts the received number into kanji using `KanjiLib.convert`
        // - creates a string "<number>, <kanji>" using the given input and converted
        // string
        // - puts the string into `generated` (unconditionally)

        Thread[] threads = new Thread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new Thread(() -> {
                while (true) {
                    try {
                        int n = queue.take();
                        if (n == POISON_PILL)
                            break;

                        String res = String.format("%d, %s", n, KanjiLib.convert(n));
                        synchronized (generated) {
                            generated.add(res);
                        }
                    } catch (InterruptedException e) {
                        // ignored
                    }
                }
            });
        }

        // TODO Start the above threads (thread A and threads B, 11 overall).

        A.start();
        for (Thread thread : threads) {
            thread.start();
        }

        // TODO Wait for each thread to finish.

        try {
            A.join();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        
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
