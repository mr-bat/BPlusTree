package Utility;


public class Utils {
    public static <T extends Comparable<T>> int searchRightmostKey(final T[] ary, final T key, final int to) {
        int low = 0;
        int high = to - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            T midVal = ary[mid];
            int cmp = midVal.compareTo(key);
            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                for (int i = mid + 1; i <= high; i++) {
                    T nxtVal = ary[i];
                    cmp = midVal.compareTo(nxtVal);
                    if (cmp != 0) {
                        break;
                    }
                    mid = i;
                }
                return mid; // key found
            }
        }
        return -(low + 1); // key not found.
    }

    public static <T extends Comparable> int searchRightmostKey(final CircularFifoQueue<T> ary, final T key, final int to) {
        int low = 0;
        int high = to - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            Comparable<T> midVal = ary.get(mid);
            int cmp = midVal.compareTo(key);
            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                for (int i = mid + 1; i <= high; i++) {
                    Comparable<T> nxtVal = ary.get(i);
                    if (midVal.equals(nxtVal)) {
                        mid = i;
                    }
                    else {
                        break;
                    }
                }
                return mid; // key found
            }
        }
        return -(low + 1); // key not found.
    }

}
