import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProducerConsumer {
    private static Queue<Integer> queue = new ConcurrentLinkedQueue<>(); // = new LinkedBlockingQueue<>();
                                                                         // new LinkedList<>();

    private static class Producer extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < 1000; ++i) {
                synchronized (queue) {
                    System.out.println("Producing: " + i);
                    queue.add(i);
                }
            }
        }
    }

    private static class Consumer extends Thread {
        private boolean running = true;

        @Override
        public void run() {
            while (running) {
                synchronized (queue) {
                    Integer num = queue.poll();
                    if (num != null) {
                        System.out.println("Consuming: " + num);
                    }
                }
            }
        }

        public void finish() {
            running = false;
        }
    }

    private static class Snapshot extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    System.out.println("Snapshot creation interrupted.");
                    // e.printStackTrace();
                    return;
                }

                synchronized (queue) {
                    StringBuilder sb = new StringBuilder("Snapshot: ");
                    for (int num : queue) {
                        sb.append(num + " ");
                    }

                    System.out.println(sb);
                }
            }
        }
    }

    public static void main(String[] args) {
        Producer producer = new Producer();
        Consumer consumer = new Consumer();
        Snapshot snapshot = new Snapshot();

        producer.start();
        consumer.start();
        snapshot.start();

        try {
            producer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        consumer.finish();
        snapshot.interrupt();
    }
}