import java.util.function.IntUnaryOperator;

public class ThreadSafeMutableIntArray {
    private ThreadSafeMutableInteger[] ints;
    private Object[] locks;

    public ThreadSafeMutableIntArray(int capacity) {
        ints = new ThreadSafeMutableInteger[capacity];
        for (int i = 0; i < capacity; i++) {
            ints[i] = new ThreadSafeMutableInteger();
        }
        locks = new Object[capacity];
        for (int i = 0; i < capacity; i++) {
            locks[i] = new Object();
        }
    }

    public final int get(int n) {
        synchronized (locks[n]) {
            return ints[n].get();
        }
    }

    public final void set(int n, int value) {
        synchronized (locks[n]) {
            ints[n].set(value);
        }
    }

    public final int updateAndGet(int n, IntUnaryOperator op) {
        synchronized(locks[n]){
            return ints[n].updateAndGet(op);
        }
    }

    public final int getAndUpdate(int n, IntUnaryOperator op) {
        synchronized(locks[n]){
            return ints[n].getAndUpdate(op);
        }
    }
}
