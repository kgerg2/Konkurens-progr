package threads;

public class ThreadInterrupter {
    public static void main(String[] args) {
        

        Thread t = new Thread(() -> {
            while (true) {
                System.out.println(
                        Thread.currentThread().getName() + ", state: " + Thread.currentThread().getState().name());
                // try {
                //     Thread.sleep(100);
                // } catch (InterruptedException e) {
                //     System.out.println(Thread.currentThread().getName() + " interrupted, state: "
                //             + Thread.currentThread().getState().name());
                //     e.printStackTrace();
                //     break;
                // }

                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
            }
        });

        System.out.println(t.getState().name());
        t.start();
        System.out.println(t.getState().name());



        for (int i = 0; i < 2; i++) {
            try {
                Thread.sleep(5);
                System.out.println(
                        Thread.currentThread().getName() + ", state: " + Thread.currentThread().getState().name());
                System.out.println(t.getName() + ", state: " + Thread.currentThread().getState().name());
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        System.out.println(t.getState().name());

        t.interrupt();

        System.out.println(t.getState().name());
        System.out.println(t.getState().name());
        System.out.println(t.getState().name());

        try {
            t.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println(t.getState().name());

        System.out.println("Main ended.");
    }
}
