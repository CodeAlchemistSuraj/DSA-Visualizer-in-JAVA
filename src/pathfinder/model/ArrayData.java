package pathfinder.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Random;

/**
 * Model class representing array data and operations. It contains no UI code
 * and notifies listeners about state changes via PropertyChangeSupport.
 */
public class ArrayData {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final int capacity;
    private final int[] data;
    private int length;
    private final Random rnd = new Random();

    public ArrayData(int capacity, int initialLength) {
        this.capacity = capacity;
        this.data = new int[capacity];
        this.length = Math.min(Math.max(0, initialLength), capacity);
    }

    public int getCapacity() { return capacity; }
    public int getLength() { return length; }
    public int[] getSnapshot() { return Arrays.copyOf(data, capacity); }
    public int get(int idx) { return data[idx]; }

    public void addPropertyChangeListener(PropertyChangeListener l) { pcs.addPropertyChangeListener(l); }
    public void removePropertyChangeListener(PropertyChangeListener l) { pcs.removePropertyChangeListener(l); }

    private void fireState(String prop, Object oldVal, Object newVal) { pcs.firePropertyChange(prop, oldVal, newVal); }

    public void randomize(int minVal, int maxVal, int minLen, int maxLen) {
        int oldLen = this.length;
        Arrays.fill(data, 0);
        this.length = Math.min(capacity, Math.max(minLen, minLen + rnd.nextInt(Math.max(1, maxLen - minLen + 1))));
        for (int i = 0; i < length; i++) data[i] = rnd.nextInt(maxVal - minVal + 1) + minVal;
        fireState("randomize", oldLen, this.length);
    }

    public boolean push(int value) {
        if (length >= capacity) return false;
        data[length++] = value;
        fireState("push", null, value);
        return true;
    }

    public Integer pop() {
        if (length <= 0) return null;
        int v = data[--length];
        data[length] = 0;
        fireState("pop", v, null);
        return v;
    }

    public boolean insert(int index, int value) {
        if (index < 0 || index > length || length >= capacity) return false;
        for (int i = length - 1; i >= index; i--) data[i + 1] = data[i];
        data[index] = value; length++;
        fireState("insert", index, value);
        return true;
    }

    public Integer delete(int index) {
        if (index < 0 || index >= length) return null;
        int old = data[index];
        for (int i = index + 1; i < length; i++) data[i - 1] = data[i];
        length--; data[length] = 0;
        fireState("delete", index, old);
        return old;
    }

    public boolean update(int index, int value) {
        if (index < 0 || index >= length) return false;
        int old = data[index];
        data[index] = value;
        fireState("update", old, value);
        return true;
    }

    public void clear() {
        Arrays.fill(data, 0);
        int old = length; length = 0; fireState("clear", old, length);
    }

    public void fill(int value) {
        for (int i = 0; i < capacity; i++) data[i] = value;
        int old = length; length = capacity; fireState("fill", old, length);
    }

    /* Algorithmic helpers (pure data changes) */
    public int linearSearch(int target) {
        for (int i = 0; i < length; i++) if (data[i] == target) { fireState("linearSearchFound", null, i); return i; }
        fireState("linearSearchNotFound", null, -1); return -1;
    }

    public int binarySearch(int target) {
        int l = 0, r = length - 1;
        while (l <= r) {
            int m = (l + r) >>> 1;
            if (data[m] == target) { fireState("binarySearchFound", null, m); return m; }
            if (data[m] < target) l = m + 1; else r = m - 1;
        }
        fireState("binarySearchNotFound", null, -1); return -1;
    }

    public int findMinIndex() {
        if (length == 0) return -1;
        int minIdx = 0;
        for (int i = 1; i < length; i++) if (data[i] < data[minIdx]) minIdx = i;
        fireState("findMin", null, minIdx); return minIdx;
    }

    public int findMaxIndex() {
        if (length == 0) return -1;
        int maxIdx = 0;
        for (int i = 1; i < length; i++) if (data[i] > data[maxIdx]) maxIdx = i;
        fireState("findMax", null, maxIdx); return maxIdx;
    }

    public int sum() {
        int s = 0; for (int i = 0; i < length; i++) s += data[i];
        fireState("sum", null, s); return s;
    }

    public double average() {
        if (length == 0) return 0.0;
        double avg = (double) sum() / length; fireState("average", null, avg); return avg;
    }

    public void reverseInPlace() {
        int i = 0, j = length - 1;
        while (i < j) { int t = data[i]; data[i] = data[j]; data[j] = t; i++; j--; }
        fireState("reverse", null, null);
    }

    public void rotateLeft(int steps) {
        if (length <= 1) { fireState("rotate", null, null); return; }
        steps = steps % length; if (steps < 0) steps += length;
        int[] tmp = new int[length];
        for (int i = 0; i < length; i++) tmp[i] = data[(i + steps) % length];
        for (int i = 0; i < length; i++) data[i] = tmp[i];
        fireState("rotateLeft", null, steps);
    }

    public void rotateRight(int steps) { rotateLeft(length - (steps % length)); }

    public boolean swapIndices(int i, int j) {
        if (i < 0 || j < 0 || i >= length || j >= length) return false;
        int t = data[i]; data[i] = data[j]; data[j] = t; fireState("swap", i, j); return true;
    }

    public int countOccurrences(int value) {
        int cnt = 0; for (int i = 0; i < length; i++) if (data[i] == value) cnt++;
        fireState("count", null, cnt); return cnt;
    }
}
