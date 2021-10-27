import java.util.Arrays;

/* Task1: slicing and merging on 1 thread, sorting slices is parralelized */
public class Task1 {

  /* Create new sorted array by merging 2 smaller sorted arrays */
  private static int[] merge(int[] arr1, int[] arr2) {
    // TODO: merge sorted arrays 'arr1' and 'arr2'
    int[] result = new int[arr1.length + arr2.length];
    int i = 0;
    int j = 0;

    while (i < arr1.length && j < arr2.length) {
      if (arr1[i] > arr2[j])
        result[i + j] = arr2[j++];
      else
        result[i + j] = arr1[i++];
    }

    while (i < arr1.length) {
      result[i + j] = arr1[i++];
    }

    while (j < arr2.length) {
      result[i + j] = arr2[j++];
    }

    return result;
  }

  /* Creates an array of arrays by slicing a bigger array into smaller chunks */
  private static int[][] slice(int[] arr, int k) {
    // TODO: cut 'arr' into 'k' smaller arrays
    int newLen = arr.length / k;
    int rem = arr.length % k;
    int[][] result = new int[k][];

    int start = 0;
    int end;
    for (int i = 0; i < k; i++) {
      end = start + newLen;
      if (rem-- > 0)
        end++;
      
      result[i] = Arrays.copyOfRange(arr, start, end);
      start = end;
    }
    return result;
  }

  /* Creates a sorted version of any int array */
  public static int[] sort(int[] array) {

    /* Initialize variables */
    // TODO: check available processors and create necessary variables
    int procs = Runtime.getRuntime().availableProcessors();
    Thread[] threads = new Thread[procs];

    /* Turn initial array into array of smaller arrays */
    // TODO: use 'slice()' method to cut 'array' into smaller bits
    int[][] split = slice(array, procs);

    /* parralelized sort on the smaller arrays */
    // TODO: use multiple threads to sort smaller arrays (Arrays.sort())
    for (int i = 0; i < procs; i++) {
      int[] arr = split[i];
      threads[i] = new Thread(() -> Arrays.sort(arr));
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

    /* Merge sorted smaller arrays into a singular larger one */
    // TODO: merge into one big array using 'merge()' multiple times
    // create an empty array called 'sorted' and in a for cycle use
    // 'merge(sorted, arr2d[i])' where arr2d is an array of sorted arrays
    int[] sorted = split[0];
    for (int i = 1; i < procs; i++) {
      sorted = merge(sorted, split[i]);
    }

    /* Return fully sorted array */
    // TODO: return the sorted array and delete all lines starting with '//'
    return sorted;
  }
}
