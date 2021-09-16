package first;

/**
 * InnerFirst
 */
class InnerFirst {

    int n;

    public void printhello() {
        System.out.println("Hello");
    }

    public InnerFirst(int n) {
        this.n = n;
    }
}

class InnerSecond extends InnerFirst {

    public InnerSecond(int n) {
        super(n);
        // TODO Auto-generated constructor stub
    }

}

class A {
    protected int N = 42;
}

class B extends A implements Runnable {

    @Override
    public void run() {
        System.out.println("B running");
        System.out.println(N);
    }
}

public class First {

    public static void main(String[] args) {
        System.out.println("Main started");

        int[] nums = new int[1];

        new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                System.out.println(nums[0]);
            }
        }).start();

        for (int i = 0; i < 100; i++) {
            new Thread(() -> nums[0]++).start();
        }

        new Thread(new B()).start();
        new Thread(() -> System.out.println("Lambda is running")).start();
        System.out.println("Main ended");
    }
}