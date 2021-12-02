import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Pipeline1 {
    public static void main(String[] args) throws Exception {
        var NO_FURTHER_INPUT1 = "";
        var NO_FURTHER_INPUT2 = Integer.MAX_VALUE;

        var bq1 = new LinkedBlockingQueue<String>();
        var bq2 = new LinkedBlockingQueue<Integer>();

        var pool = Executors.newCachedThreadPool();

        pool.submit(() -> {
            bq1.addAll(List.of("a", "bb", "ccccccc", "ddd", "eeee", NO_FURTHER_INPUT1));
        });

        pool.submit(() -> {
            try {
                while (true) {
                    String string = bq1.take();
                    if (string == NO_FURTHER_INPUT1) {
                        bq2.put(NO_FURTHER_INPUT2);
                        break;
                    }
                    bq2.put(string.length());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        pool.submit(() -> {
            try {
                while (true) {
                    int len = bq2.take();
                    if (len == NO_FURTHER_INPUT2) {
                        break;
                    }
                    System.out.println(len);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
    }
}
