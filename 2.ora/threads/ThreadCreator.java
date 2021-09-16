package threads;

import java.util.ArrayList;

public class ThreadCreator {
    public static void main(String[] args) {
        ArrayList<Thread> threads = new ArrayList<Thread>();

        for (int i = 0; i < 1000; i++) {
            threads.add(new Thread(() -> {
                System.out.println("Running: " + Thread.currentThread().getName());
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

        System.out.println("Main ended");
    }
}
