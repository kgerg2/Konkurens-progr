import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static final int CLIENT_NUM = 10;
    public static final int THREAD_NUM = 8;
    public static final int ROUND_NUM = 10000;

    public static void main(String[] args) {
        AtomicInteger bank = new AtomicInteger();

        ArrayList<Future<Integer>> loans = new ArrayList<Future<Integer>>(CLIENT_NUM);

        ExecutorService service = Executors.newFixedThreadPool(THREAD_NUM);

        for (int i = 0; i < CLIENT_NUM; i++) {
            loans.add(service.submit(() -> {
                int sum = 0;
                for (int j = 0; j < ROUND_NUM; j++) {
                    int loan = Math.abs(ThreadLocalRandom.current().nextInt()) % 1000;
                    bank.getAndAdd(loan);
                    sum += loan;
                }
                return sum;
            }));
        }

        int sum = 0;

        for (Future<Integer> future : loans) {
            try {
                sum += future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println(bank.get());
        System.out.println(sum);

        if (sum != bank.get()) {
            System.out.println("Not equal. :(");
        }

        service.shutdown();
        // try {
        //     service.awaitTermination(1, TimeUnit.MINUTES);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
    }

    public static void main2(String[] args) {
        Bank bank = new Bank();

        int[] loans = new int[CLIENT_NUM];

        ExecutorService service = Executors.newFixedThreadPool(THREAD_NUM);

        for (int i = 0; i < CLIENT_NUM; i++) {
            int I = i;
            service.submit(() -> {
                int sum = 0;
                for (int j = 0; j < ROUND_NUM; j++) {
                    int loan = Math.abs(ThreadLocalRandom.current().nextInt()) % 1000;
                    bank.takeLoan(loan);
                    sum += loan;
                }
                loans[I] = sum;
            });
        }

        service.shutdown();
        try {
            service.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(bank.getLoans());
        System.out.println(Arrays.stream(loans).sum());
    }

    public static void main3(String[] args) {
        Bank bank = new Bank();

        ArrayList<Future<Integer>> loans = new ArrayList<Future<Integer>>(CLIENT_NUM);

        ExecutorService service = Executors.newFixedThreadPool(THREAD_NUM);

        for (int i = 0; i < CLIENT_NUM; i++) {
            loans.add(service.submit(() -> {
                int sum = 0;
                for (int j = 0; j < ROUND_NUM; j++) {
                    int loan = Math.abs(ThreadLocalRandom.current().nextInt()) % 1000;
                    bank.takeLoan(loan);
                    sum += loan;
                }
                return sum;
            }));
        }

        int sum = 0;

        for (Future<Integer> future : loans) {
            try {
                sum += future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println(bank.getLoans());
        System.out.println(sum);

        if (sum != bank.getLoans()) {
            System.out.println("Not equal. :(");
        }

        service.shutdown();
        try {
            service.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
