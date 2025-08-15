package pathfinder.algorithms;

public class BinarySearchUtil {
    // Returns index of target in sorted array or -1 if not found
    public static int binarySearch(int[] a, int target) {
        if (a == null) return -1;
        int l = 0, r = a.length - 1;
        while (l <= r) {
            int m = (l + r) >>> 1;
            if (a[m] == target) return m;
            if (a[m] < target) l = m + 1; else r = m - 1;
        }
        return -1;
    }
}
