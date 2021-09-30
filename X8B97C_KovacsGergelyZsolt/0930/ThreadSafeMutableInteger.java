import java.util.function.IntUnaryOperator;

public class ThreadSafeMutableInteger {
    private int n;

    public ThreadSafeMutableInteger() {
        this(0);
    }

    public ThreadSafeMutableInteger(int n) {
        this.set(n);
    }

    public final synchronized int get() {
        return n;
    }

    public final synchronized void set(int n) {
        this.n = n;
    }

    public final synchronized int getAndIncrement() {
        return n++;
    }

    public final synchronized int getAndDecrement() {
        return n--;
    }

    public final synchronized int getAndAdd(int v) {
        int temp = n;
        n += v;
        return temp;
    }

    public final synchronized int incrementAndGet() {
        return ++n;
    }

    public final synchronized int decrementAndGet() {
        return --n;
    }

    public final synchronized int getAndUpdate(IntUnaryOperator op) {
        int temp = n;
        n = op.applyAsInt(n);
        return temp;
    }

    public final synchronized int updateAndGet(IntUnaryOperator op) {
        n = op.applyAsInt(n);
        return n;
    }
}
