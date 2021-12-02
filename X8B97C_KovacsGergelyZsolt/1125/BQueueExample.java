import java.util.NoSuchElementException;
import java.util.concurrent.*;

class BQueueExample1 {

    private static final int POISON_PILL = Integer.MAX_VALUE;

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> bq = new LinkedBlockingQueue<>(1000);
        var pool = Executors.newFixedThreadPool(1);
        Future<?> taskRes = pool.submit(() -> {
            while (true) {
                Integer v = null;
                try {
                    v = bq.take();
                } catch (InterruptedException e) {
                    System.out.println("Interrupted.");
                    break;
                }
                if (v == POISON_PILL) {
                    System.out.println("Got poison pill. Shutting down.");
                    break;
                }
                System.out.println(v);
            }
        });

        pool.shutdown();
        for (int i = 1; i <= 10000; i++) {
            bq.put(i);
        }
        System.out.println(pool.isTerminated());
        bq.put(POISON_PILL);
//        taskRes.cancel(true);
        Thread.sleep(1);
        System.out.println(pool.isTerminated());
    }
}


class BQueueExample2_1 {

    private static final int POISON_PILL = Integer.MAX_VALUE;

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> bq = new LinkedBlockingQueue<>(100);
        var pool = Executors.newFixedThreadPool(1);
        Future<?> taskRes = pool.submit(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Interrupted.");
                    break;
                }
                Integer v = bq.poll();
                if (v == null) {
                    System.out.println("Queue empty. No new value.");
                    continue;
                }
                if (v == POISON_PILL) {
                    System.out.println("Got poison pill. Shutting down.");
                    break;
                }
                System.out.println(v);
            }
        });

        pool.shutdown();
        for (int i = 1; i <= 10000; i++) {
            boolean success = bq.offer(i);
            if (!success) {
                System.out.println("Queue full. Value lost: " + i);
            }
        }
        System.out.println(pool.isTerminated());
        bq.put(POISON_PILL);
//        taskRes.cancel(true);
        Thread.sleep(1);
        System.out.println(pool.isTerminated());
    }
}


class BQueueExample2_2 {

    private static final int POISON_PILL = Integer.MAX_VALUE;

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> bq = new LinkedBlockingQueue<>(1);
        var pool = Executors.newFixedThreadPool(1);
        Future<?> taskRes = pool.submit(() -> {
            while (true) {
                Integer v = null;
                try {
                    v = bq.poll(1, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    System.out.println("Interrupted.");
                    break;
                }
                if (v == null) {
                    System.out.println("Queue empty. No new value.");
                    continue;
                }
                if (v == POISON_PILL) {
                    System.out.println("Got poison pill. Shutting down.");
                    break;
                }
                System.out.println(v);
            }
        });

        pool.shutdown();
        for (int i = 1; i <= 10000; i++) {
            boolean success = bq.offer(i, 1, TimeUnit.MILLISECONDS);
            if (!success) {
                System.out.println("Queue full. Value lost: " + i);
            }
        }
        System.out.println(pool.isTerminated());
        bq.put(POISON_PILL);
//        taskRes.cancel(true);
        Thread.sleep(1);
        System.out.println(pool.isTerminated());
    }
}


class BQueueExample3 {

    private static final int POISON_PILL = Integer.MAX_VALUE;

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> bq = new LinkedBlockingQueue<>(1000);
        var pool = Executors.newFixedThreadPool(1);
        Future<?> taskRes = pool.submit(() -> {
            while (true) {
                Integer v = null;
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Interrupted.");
                    break;
                }
                try {
                    v = bq.remove();
                } catch (NoSuchElementException e) {
                    System.out.println("Queue empty. No new value.");
                    continue;
                }
                if (v == POISON_PILL) {
                    System.out.println("Got poison pill. Shutting down.");
                    break;
                }
                System.out.println(v);
            }
        });

        pool.shutdown();
        for (int i = 1; i <= 10000; i++) {
            try {
                bq.add(i);
            } catch (IllegalStateException e) {
                System.out.println("Queue full. Value lost: " + i);
            }
        }
        System.out.println(pool.isTerminated());
        bq.put(POISON_PILL);
//        taskRes.cancel(true);
        Thread.sleep(1);
        System.out.println(pool.isTerminated());
    }
}
