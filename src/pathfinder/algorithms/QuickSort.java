package pathfinder.algorithms;

import java.util.Random;

public class QuickSort {
    private static final Random rnd = new Random();

    public static void sort(int[] a) {
        if (a == null || a.length < 2) return;
        quick(a, 0, a.length - 1);
    }

    private static void quick(int[] a, int l, int r) {
        if (l >= r) return;
        int p = partition(a, l, r);
        quick(a, l, p - 1);
        quick(a, p + 1, r);
    }

    private static int partition(int[] a, int l, int r) {
        int pivotIndex = l + rnd.nextInt(r - l + 1);
        int pivot = a[pivotIndex];
        swap(a, pivotIndex, r);
        int store = l;
        for (int i = l; i < r; i++) {
            if (a[i] < pivot) {
                swap(a, i, store++);
            }
        }
        swap(a, store, r);
        return store;
    }

    private static void swap(int[] a, int i, int j) { int t = a[i]; a[i] = a[j]; a[j] = t; }
}
