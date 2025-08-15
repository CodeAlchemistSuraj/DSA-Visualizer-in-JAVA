package pathfinder.algorithms;

public class HeapSort {
    public static void sort(int[] a) {
        if (a == null || a.length < 2) return;
        int n = a.length;
        // build max heap
        for (int i = n / 2 - 1; i >= 0; i--) siftDown(a, i, n);
        for (int end = n - 1; end > 0; end--) {
            swap(a, 0, end);
            siftDown(a, 0, end);
        }
    }

    private static void siftDown(int[] a, int i, int n) {
        while (true) {
            int l = 2 * i + 1;
            int r = l + 1;
            int largest = i;
            if (l < n && a[l] > a[largest]) largest = l;
            if (r < n && a[r] > a[largest]) largest = r;
            if (largest == i) break;
            swap(a, i, largest);
            i = largest;
        }
    }

    private static void swap(int[] a, int i, int j) { int t = a[i]; a[i] = a[j]; a[j] = t; }
}
