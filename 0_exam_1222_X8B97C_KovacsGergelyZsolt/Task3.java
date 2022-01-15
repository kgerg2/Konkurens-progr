import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class Task3 {
    private static final int NUM_THREADS = 10;
    private static final int CHANNEL_CAPACITY = 100;
    private static final int POISON_PILL = -1;
    private static final int MAX_WAIT_SEND_NUM = 100;
    private static final int MAX_WAIT_SEND_ET = 10;

    // TODO Declare a thread-safe data structure for holding result named
    // `generated`.
    private Queue<String> generated = new ConcurrentLinkedQueue<>();

    // TODO Declare a data structure for holding 10 "B" threads.
    private List<Thread> B = new ArrayList<>(NUM_THREADS);

    // TODO Declare thread reference for "A".
    private Thread A;

    // TODO Define a data structure that will be used as a bounded communication
    // channel between threads
    // the maximal capacity of the channel must be `CHANNEL_CAPACITY`.
    private BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(CHANNEL_CAPACITY);

    public List<String> get() throws InterruptedException {
        // TODO Wait for all threads to finish ("A" and all "B" threads).
        // `InterruptedException` should be propagated (not caught).
        A.join();
        for (Thread thread : B) {
            thread.join();
        }
        // TODO Return a `List` of `String` containing the data from `generated`.
        // Note: Conversion to plain `List` is needed since `generated` should have some
        // other (thread-safe) type.
        return new ArrayList<>(generated);
    }

    public List<Thread> getThreads() {
        // TODO Return the references of the 10 "B" threads.
        return B;
    }

    public void interrupt() {
        // TODO Interrupt the random generation ("A") thread.
        A.interrupt();
    }

    public Task3(final int from, final int to, final int count) {
        if (from < 0 || to < 0 || !isInRange(count, 0, to - from + 1))
            throw new IllegalArgumentException();

        // TODO Implement the same logic as in Task2 with the following modifications:

        // TODO If thread "A" is interrupted it should quit sending numbers and
        // send `POISON_PILL` to others immediately.

        // TODO Random number generation should be implemented in the `generateNum`
        // method.

        // TODO Sending `POISON_PILL`s should be implemented in the `sendPoisonPill`
        // method.

        // TODO Thread "A" should wait up to `MAX_WAIT_SEND_NUM` milliseconds to send a
        // number.
        // If thread "A" fails to send due to timeout, it should finish immediately.

        // TODO "B" threads should also send `POISON_PILL` when interrupted and then
        // quit.

        // TODO Start threads, but DO NOT wait for them here to finish.

        A = new Thread(() -> {
            Set<Integer> set = new HashSet<Integer>();
            while (set.size() < count) {
                int n = generateNum(from, to, set);
                if (set.add(n)) {
                    try {
                        if (!queue.offer(n, MAX_WAIT_SEND_NUM, TimeUnit.MILLISECONDS))
                            return;
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
            sendPoisonPill();
        });

        for (int i = 0; i < NUM_THREADS; i++) {
            B.add(new Thread(() -> {
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
                        sendPoisonPill();
                        break;
                    }
                }
            }));
        }

        A.start();

        for (Thread thread : B) {
            thread.start();
        }
    }

    private void sendPoisonPill() {
        // TODO Send `POISON_PILL` via the channel to each of the 10 "B" threads.
        // TODO After `MAX_WAIT_SEND_ET` time give up and continue to the next thread.
        for (int i = 0; i < NUM_THREADS; i++) {
            try {
                queue.offer(POISON_PILL, MAX_WAIT_SEND_ET, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // ignored
            }
        }
    }

    private int generateNum(int from, int to, Set<Integer> /* Note: Suggested type, can be modified. */ sent) {
        // TODO Move random number generation here.
        int n;
        do {
            n = from + (int) (Math.random() * (to - from + 1));
        } while (sent.contains(n));
        return n; // TODO Return the generated unique random number.
    }

    private static boolean isInRange(int count, int from, int to) {
        return from <= count && count <= to;
    }
}
