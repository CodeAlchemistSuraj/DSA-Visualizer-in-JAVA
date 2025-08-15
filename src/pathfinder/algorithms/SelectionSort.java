package pathfinder.algorithms;

public class SelectionSort {
    public static void sort(int[] a) {
        if (a == null) return;
        for (int i = 0; i < a.length; i++) {
            int min = i;
            for (int j = i + 1; j < a.length; j++) if (a[j] < a[min]) min = j;
            int t = a[i]; a[i] = a[min]; a[min] = t;
        }
    }
}
