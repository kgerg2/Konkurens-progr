import java.util.Arrays;

/* Task2: no slicing, no bullshit memcopy, parralelized merge */
public class Task2 {

  /* Create new sorted array by merging 2 smaller sorted arrays */
  private static void merge(int[] src, int[] dst, int idx1, int idx2, int end) {

    int i = idx1; // iterates over src 1st half
    int j = idx2; // iterates over src 2nd half
    int k = idx1; // iterates over dst

    while (i < idx2 && j < end) {
      if (src[i] > src[j])
        dst[k++] = src[j++];
      else
        dst[k++] = src[i++];
    }

    while (i < idx2) {
      dst[k++] = src[i++];
    }

    while (j < end) {
      dst[k++] = src[j++];
    }
  }

  /* Recursive core, calls a sibling thread until max depth is reached */
  public static void kernel(int[] src, int[] dst, int start, int end, int depth) {

    /* Single thread sort if bottom has been reached */
    if (depth == 0) {
      Arrays.sort(src, start, end);
      return;
    }

    /* Otherwise split task into two recursively */
    int middle = (start + end) / 2;
    Thread fisrt = new Thread(() -> kernel(src, dst, start, middle, depth - 1));
    fisrt.start();

    kernel(src, dst, middle, end, depth - 1);

    try {
      fisrt.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    if (depth % 2 == 0)
      merge(dst, src, start, middle, end);
    else
      merge(src, dst, start, middle, end);
  }

  /* Creates a sorted version of any int array */
  public static int[] sort(int[] array) {

    /* Initialize variables */
    int[] src = array.clone();
    int[] dst = new int[array.length];

    /* Calculate optimal depth */
    int minSize = 1000;
    int procNum = Runtime.getRuntime().availableProcessors();
    int procDepth = (int) Math.ceil(Math.log(procNum) / Math.log(2));
    int arrDepth = (int) (Math.log(array.length / minSize) / Math.log(2));
    int optDepth = Math.max(0, Math.min(procDepth, arrDepth));

    /* Launch recursive core */
    kernel(src, dst, 0, array.length, optDepth);

    return optDepth % 2 == 0 ? src : dst;
  }
}
