package pathfinder.algorithms;

public class MergeSort {
    public static void sort(int[] a) {
        if (a == null || a.length < 2) return;
        int[] tmp = new int[a.length];
        sort(a, tmp, 0, a.length - 1);
    }

    private static void sort(int[] a, int[] tmp, int l, int r) {
        if (l >= r) return;
        int m = (l + r) >>> 1;
        sort(a, tmp, l, m);
        sort(a, tmp, m + 1, r);
        merge(a, tmp, l, m, r);
    }

    private static void merge(int[] a, int[] tmp, int l, int m, int r) {
        int i = l, j = m + 1, k = l;
        while (i <= m && j <= r) {
            if (a[i] <= a[j]) tmp[k++] = a[i++]; else tmp[k++] = a[j++];
        }
        while (i <= m) tmp[k++] = a[i++];
        while (j <= r) tmp[k++] = a[j++];
        for (i = l; i <= r; i++) a[i] = tmp[i];
    }
}
