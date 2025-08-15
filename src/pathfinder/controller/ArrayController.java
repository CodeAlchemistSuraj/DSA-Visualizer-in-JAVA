package pathfinder.controller;

import pathfinder.model.ArrayData;
import pathfinder.util.Animator;
import java.util.function.IntSupplier;

/**
 * Simple controller that operates on ArrayData. The UI (ArrayView/ArrayPanel) can attach as a listener
 * to the model and use Animator for step-wise animations.
 */
public class ArrayController {
    private final ArrayData model;
    private final Animator animator = new Animator();

    public ArrayController(ArrayData model) { this.model = model; }

    public void push(int v, IntSupplier delay) { model.push(v); }
    public Integer pop(IntSupplier delay) { return model.pop(); }
    public boolean insert(int idx, int v, IntSupplier delay) { return model.insert(idx, v); }
    public Integer delete(int idx, IntSupplier delay) { return model.delete(idx); }
    public boolean update(int idx, int v, IntSupplier delay) { return model.update(idx, v); }
    public void randomize(int min, int max, int minLen, int maxLen) { model.randomize(min, max, minLen, maxLen); }
    public void clear() { model.clear(); }
    public void fill(int v) { model.fill(v); }
    public int linearSearch(int target) { return model.linearSearch(target); }
    public int binarySearch(int target) { return model.binarySearch(target); }
    public int findMin() { return model.findMinIndex(); }
    public int findMax() { return model.findMaxIndex(); }
    public int sum() { return model.sum(); }
    public double average() { return model.average(); }
    public void reverse() { model.reverseInPlace(); }
    public void rotateLeft(int steps) { model.rotateLeft(steps); }
    public void rotateRight(int steps) { model.rotateRight(steps); }
    public boolean swap(int i, int j) { return model.swapIndices(i, j); }
    public int countOccurrences(int v) { return model.countOccurrences(v); }

    public Animator getAnimator() { return animator; }
}
