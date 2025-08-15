package pathfinder.algorithms;

public class HeapUtils {
    // In-place heapify for 0-based array
    public static void heapify(int[] a) {
        int n = a.length;
        for (int i = n/2 - 1; i >= 0; i--) siftDown(a, i, n);
    }

    private static void siftDown(int[] a, int i, int n) {
        while (true) {
            int l = 2*i + 1; int r = l + 1; int largest = i;
            if (l < n && a[l] > a[largest]) largest = l;
            if (r < n && a[r] > a[largest]) largest = r;
            if (largest == i) break;
            int t = a[i]; a[i] = a[largest]; a[largest] = t; i = largest;
        }
    }
}
