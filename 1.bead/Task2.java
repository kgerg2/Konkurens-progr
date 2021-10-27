import java.util.Arrays;

/* Task2: no slicing, no bullshit memcopy, parralelized merge */
public class Task2 {

  /* Create new sorted array by merging 2 smaller sorted arrays */
  private static void merge(int[] src, int[] dst, int idx1, int idx2, int end) {
    // TODO: 'src' is sorted between [idx1,idx2) and [idx2,end)
    // copy both to 'dst' in a way that [idx1,end) is sorted for 'dst'
    // Note: 'idx1' is the starting point of the 1st array
    // 'idx2' is the starting point of the 2nd array
    // 'end' is the end of the 2nd array (exclusive)
    // There are no elements between the first and second arrays
    // 'src' is the source, this is where the 2 smaller sorted arrays are
    // 'dst' is the destination, this is where you have to move data
    // Merge the 2 smaller arrays using the same methodology as in 'Task1'

    int i = idx1;
    int j = idx2;
    int k = idx1;
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
    // TODO: simply sort the array using 'Arrays.sort()' if depth is zero.
    if (depth == 0) {
      Arrays.sort(src, start, end);
      return;
    }

    /* Otherwise split task into two recursively */
    // TODO: summon another thread and recursively sort left and right half
    // NOTE: don't forget to make recursive call with 'depth-1'
    int middle = (start + end) / 2;
    Thread fisrt = new Thread(() -> kernel(src, dst, start, middle, depth - 1));
    Thread second = new Thread(() -> kernel(src, dst, middle, end, depth - 1));
    fisrt.start();
    second.start();

    try {
      fisrt.join();
      second.join();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
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
    // TODO: Create 'src' and 'dst' arrays
    int[] src = array.clone();
    int[] dst = new int[array.length];

    /* Calculate optimal depth */
    int minSize = 1000;
    int procNum = Runtime.getRuntime().availableProcessors();
    int procDepth = (int) Math.ceil(Math.log(procNum) / Math.log(2));
    int arrDepth = (int) (Math.log(array.length / minSize) / Math.log(2));
    int optDepth = Math.max(0, Math.min(procDepth, arrDepth));

    /* Launch recursive core */
    // TODO: launch kernel, call with 'optDepth' (not 'optDepth-1')
    kernel(src, dst, 0, array.length, optDepth);

    // TODO: return src or dst depending on the parity of the used depth
    // TODO: delete all lines starting with '//'
    if (optDepth % 2 == 0)
      return src;
    else
      return dst;
  }
}
