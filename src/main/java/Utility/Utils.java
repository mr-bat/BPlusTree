package Utility;


public class Utils {
    /**
     * @return returns the leftmost value greater than or equal to key
     */
    public static <T extends Comparable> int searchLeftmostKey(final CircularFifoQueue<T> ary, final T key, final int to) {
        if (to <= 0)
            return -1;

        int low = 0;
        int high = to;
        while (low < high) {
            int mid = (low + high) >>> 1;
            Comparable<T> midVal = ary.get(mid);
            int cmp = midVal.compareTo(key);
            if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return low < to && ary.get(low).compareTo(key) == 0 ? low : -(low + 1); // key not found.
    }

    /**
     * @return returns the rightmost value lower or equal to key
     */
    public static <T extends Comparable> int searchRightmostKey(final CircularFifoQueue<T> ary, final T key, final int to) {
        if (to <= 0)
            return -1;

        int low = 0;
        int high = to - 1;
        while (low < high) {
            int mid = (low + high + 1) >>> 1;
            Comparable<T> midVal = ary.get(mid);
            int cmp = midVal.compareTo(key);
            if (cmp <= 0) {
                low = mid;
            } else {
                high = mid - 1;
            }
        }
        return ary.get(low).compareTo(key) == 0 ? low : -(low + 1); // key not found.
    }
}
