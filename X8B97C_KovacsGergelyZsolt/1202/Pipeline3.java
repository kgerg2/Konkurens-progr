import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Pipeline3 {
    static int NO_FURTHER_INPUT = Integer.MAX_VALUE;

    private static <T> List<T> nCopyList(int count, IntFunction<T> makeElem) {
        return IntStream.range(0, count).mapToObj(i -> makeElem.apply(i)).collect(Collectors.toList());
    }

    public static void main(String[] args) throws Exception {
        int bound = 100;
        int stageCount = 7;

        var queues = nCopyList(stageCount + 1, n -> new ArrayBlockingQueue<Integer>(1000));

        initQueue(bound, queues.get(0));

        int[] queuedPrimes = new int[stageCount];

        var callables = new ArrayList<Callable<List<Integer>>>();
        for (int i = 0; i < stageCount; i++) {
            var idx = i;
            callables.add(() -> {
                var nonPrimes = new ArrayList<Integer>();

                try {
                    queuedPrimes[idx] = queues.get(idx).take();

                    var isOn = true;
                    while (isOn) {
                        int num = queues.get(idx).take();
                        if (num == Integer.MAX_VALUE) {
                            queues.get(idx+1).put(Integer.MAX_VALUE);
                            break;
                        }

                        if (num % queuedPrimes[idx] == 0) {
                            nonPrimes.add(num);
                        } else {
                            queues.get(idx+1).put(num);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return nonPrimes;
            });
        }

        var pool = Executors.newCachedThreadPool();
        var futures = pool.invokeAll(callables);
        for (int i = 0; i < stageCount; i++) {
            System.out.printf("Filtered by %d: %s%n", queuedPrimes[i], futures.get(i).get());
        }

        var remainingPrimes = new ArrayList<>();
        queues.get(stageCount).drainTo(remainingPrimes);
        System.out.printf("Remaining: %s%n", remainingPrimes);

        pool.shutdown();
    }

    private static void initQueue(int bound, ArrayBlockingQueue<Integer> queue0) {
        for (int i = 0; i < bound; i++) {
            queue0.add(i * 2 + 3);
        }
        queue0.add(Integer.MAX_VALUE);
    }
}
