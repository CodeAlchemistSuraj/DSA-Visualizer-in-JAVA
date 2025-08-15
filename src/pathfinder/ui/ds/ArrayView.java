package pathfinder.ui.ds;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

public class ArrayView extends JPanel {
	private final JLabel statusLabel = new JLabel("Ready. Use controls to modify the array.");
	// Model and controller (refactored)
	private final pathfinder.model.ArrayData arrayData = new pathfinder.model.ArrayData(20, 12);
	private final pathfinder.controller.ArrayController arrayController = new pathfinder.controller.ArrayController(arrayData);
	private final ArrayPanel arrayPanel = new ArrayPanel(this::setStatusText, arrayData);
	private final JButton randomizeButton = new JButton("Randomize");
	private final JButton pushButton = new JButton("Push (end)");
	private final JButton popButton = new JButton("Pop (end)");
	private final JButton unshiftButton = new JButton("Unshift (front)");
	private final JButton shiftButton = new JButton("Shift (front)");
	private final JButton insertButton = new JButton("Insert at index");
	private final JButton deleteButton = new JButton("Delete at index");
	private final JButton updateButton = new JButton("Update at index");
	private final JSpinner valueSpinner = new JSpinner(new SpinnerNumberModel(50, -999, 999, 1));
	private final JSpinner indexSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 19, 1));
	private final JSlider speedSlider = new JSlider(0, 200, 20);

	// New controls for additional array operations
	private final JButton linearSearchButton = new JButton("Linear Search");
	private final JButton binarySearchButton = new JButton("Binary Search");
	private final JButton findMinButton = new JButton("Find Min");
	private final JButton findMaxButton = new JButton("Find Max");
	private final JButton sumButton = new JButton("Sum");
	private final JButton avgButton = new JButton("Average");
	private final JButton reverseButton = new JButton("Reverse (in-place)");
	private final JButton rotateLeftButton = new JButton("Rotate Left");
	private final JButton rotateRightButton = new JButton("Rotate Right");
	private final JButton swapButton = new JButton("Swap indices");
	private final JButton countButton = new JButton("Count occurrences");
	private final JButton clearButton = new JButton("Clear");
	private final JButton fillButton = new JButton("Fill");
	private final JSpinner indexSpinner2 = new JSpinner(new SpinnerNumberModel(0, 0, 19, 1));
	private final JSpinner rotateStepsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 19, 1));

	public ArrayView() {
		super(new BorderLayout());
		JPanel controls = new JPanel();
		controls.add(new JLabel("Value:"));
		controls.add(valueSpinner);
		controls.add(new JLabel("Index:"));
		controls.add(indexSpinner);
		controls.add(new JLabel("Index2:"));
		controls.add(indexSpinner2);
		controls.add(insertButton);
		controls.add(deleteButton);
		controls.add(updateButton);
		controls.add(pushButton);
		controls.add(popButton);
		controls.add(unshiftButton);
		controls.add(shiftButton);
		controls.add(new JLabel("Steps:"));
		controls.add(rotateStepsSpinner);
		controls.add(rotateLeftButton);
		controls.add(rotateRightButton);
		controls.add(reverseButton);
		controls.add(swapButton);
		controls.add(linearSearchButton);
		controls.add(binarySearchButton);
		controls.add(findMinButton);
		controls.add(findMaxButton);
		controls.add(sumButton);
		controls.add(avgButton);
		controls.add(countButton);
		controls.add(clearButton);
		controls.add(fillButton);
		controls.add(randomizeButton);
		controls.add(new JLabel("Speed:"));
		speedSlider.setInverted(true);
		controls.add(speedSlider);
		add(controls, BorderLayout.NORTH);
		add(arrayPanel, BorderLayout.CENTER);

		JPanel bottom = new JPanel(new BorderLayout());
		bottom.setBorder(BorderFactory.createEmptyBorder(4, 8, 6, 8));
		bottom.add(buildLegend(), BorderLayout.NORTH);
		bottom.add(statusLabel, BorderLayout.SOUTH);
		add(bottom, BorderLayout.SOUTH);

		wire();
	}

	private JPanel buildLegend() {
		JPanel legend = new JPanel();
		legend.add(coloredBox(new Color(230, 230, 230), "Empty slot"));
		legend.add(coloredBox(new Color(0, 123, 255, 170), "Filled value"));
		legend.add(coloredBox(new Color(255, 193, 7), "Actively moved/compared"));
		legend.add(coloredBox(new Color(40, 167, 69), "Target index"));
		legend.add(new JLabel("Pointers: i, j shown above cells"));
		return legend;
	}

	private JPanel coloredBox(Color c, String text) {
		JPanel p = new JPanel();
		p.setPreferredSize(new Dimension(16, 16));
		p.setBackground(c);
		JPanel wrap = new JPanel();
		wrap.add(p);
		wrap.add(new JLabel(" " + text + "  "));
		return wrap;
	}

	private void wire() {
	randomizeButton.addActionListener(e -> { arrayController.randomize(10, 99, 12, 16); setStatusText("Randomized array and length."); arrayPanel.refreshFromModel(); });
	pushButton.addActionListener(e -> runAsync(() -> { arrayController.push((Integer) valueSpinner.getValue(), this::delay); arrayPanel.refreshFromModel(); }));
	popButton.addActionListener(e -> runAsync(() -> { arrayController.pop(this::delay); arrayPanel.refreshFromModel(); }));
	unshiftButton.addActionListener(e -> runAsync(() -> { /* keep existing UI animation for now */ arrayPanel.unshift((Integer) valueSpinner.getValue(), this::delay); arrayPanel.refreshFromModel(); }));
	shiftButton.addActionListener(e -> runAsync(() -> { arrayPanel.shift(this::delay); arrayPanel.refreshFromModel(); }));
	insertButton.addActionListener(e -> runAsync(() -> { arrayController.insert((Integer) indexSpinner.getValue(), (Integer) valueSpinner.getValue(), this::delay); arrayPanel.refreshFromModel(); }));
	deleteButton.addActionListener(e -> runAsync(() -> { arrayController.delete((Integer) indexSpinner.getValue(), this::delay); arrayPanel.refreshFromModel(); }));
	updateButton.addActionListener(e -> runAsync(() -> { arrayController.update((Integer) indexSpinner.getValue(), (Integer) valueSpinner.getValue(), this::delay); arrayPanel.refreshFromModel(); }));

	// New wiring (some still use ArrayPanel animations until full migration)
	linearSearchButton.addActionListener(e -> runAsync(() -> { int idx = arrayController.linearSearch((Integer) valueSpinner.getValue()); setStatusText(idx>=0?"Found at " + idx : "Not found"); arrayPanel.refreshFromModel(); }));
	binarySearchButton.addActionListener(e -> runAsync(() -> { int idx = arrayController.binarySearch((Integer) valueSpinner.getValue()); setStatusText(idx>=0?"Found at " + idx : "Not found"); arrayPanel.refreshFromModel(); }));
	findMinButton.addActionListener(e -> runAsync(() -> { int idx = arrayController.findMin(); setStatusText(idx>=0?"Min at " + idx : "Empty"); arrayPanel.refreshFromModel(); }));
	findMaxButton.addActionListener(e -> runAsync(() -> { int idx = arrayController.findMax(); setStatusText(idx>=0?"Max at " + idx : "Empty"); arrayPanel.refreshFromModel(); }));
	sumButton.addActionListener(e -> runAsync(() -> { int s = arrayController.sum(); setStatusText("Sum = " + s); arrayPanel.refreshFromModel(); }));
	avgButton.addActionListener(e -> runAsync(() -> { double a = arrayController.average(); setStatusText("Average = " + String.format("%.2f", a)); arrayPanel.refreshFromModel(); }));
	reverseButton.addActionListener(e -> runAsync(() -> { arrayController.reverse(); setStatusText("Reversed"); arrayPanel.refreshFromModel(); }));
	rotateLeftButton.addActionListener(e -> runAsync(() -> { arrayController.rotateLeft((Integer) rotateStepsSpinner.getValue()); arrayPanel.refreshFromModel(); }));
	rotateRightButton.addActionListener(e -> runAsync(() -> { arrayController.rotateRight((Integer) rotateStepsSpinner.getValue()); arrayPanel.refreshFromModel(); }));
	swapButton.addActionListener(e -> runAsync(() -> { arrayController.swap((Integer) indexSpinner.getValue(), (Integer) indexSpinner2.getValue()); arrayPanel.refreshFromModel(); }));
	countButton.addActionListener(e -> runAsync(() -> { int cnt = arrayController.countOccurrences((Integer) valueSpinner.getValue()); setStatusText("Count = " + cnt); arrayPanel.refreshFromModel(); }));
	clearButton.addActionListener(e -> runAsync(() -> { arrayController.clear(); arrayPanel.refreshFromModel(); }));
	fillButton.addActionListener(e -> runAsync(() -> { arrayController.fill((Integer) valueSpinner.getValue()); arrayPanel.refreshFromModel(); }));
	}

	private void runAsync(Runnable r) {
		setButtonsEnabled(false);
		new Thread(() -> {
			try {
				r.run();
			} finally {
				SwingUtilities.invokeLater(() -> setButtonsEnabled(true));
			}
		}).start();
	}

	private void setButtonsEnabled(boolean enabled) {
		pushButton.setEnabled(enabled);
		popButton.setEnabled(enabled);
		unshiftButton.setEnabled(enabled);
		shiftButton.setEnabled(enabled);
		insertButton.setEnabled(enabled);
		deleteButton.setEnabled(enabled);
		updateButton.setEnabled(enabled);
		randomizeButton.setEnabled(enabled);
		linearSearchButton.setEnabled(enabled);
		binarySearchButton.setEnabled(enabled);
		findMinButton.setEnabled(enabled);
		findMaxButton.setEnabled(enabled);
		sumButton.setEnabled(enabled);
		avgButton.setEnabled(enabled);
		reverseButton.setEnabled(enabled);
		rotateLeftButton.setEnabled(enabled);
		rotateRightButton.setEnabled(enabled);
		swapButton.setEnabled(enabled);
		countButton.setEnabled(enabled);
		clearButton.setEnabled(enabled);
		fillButton.setEnabled(enabled);
	}

	private int delay() { return Math.max(1, speedSlider.getMaximum() - speedSlider.getValue() + 1); }
	private void setStatusText(String t) { SwingUtilities.invokeLater(() -> statusLabel.setText(t)); }

	static class ArrayPanel extends JPanel {
		private final int capacity = 20;
		private final int[] data = new int[capacity];
		private int length = 12;
		private final Random rnd = new Random();
		private final Consumer<String> status;
		private final pathfinder.model.ArrayData model;
		private int pointerI = -1, pointerJ = -1, targetIdx = -1, highlightA = -1;

		ArrayPanel(Consumer<String> status, pathfinder.model.ArrayData model) {
			this.status = status;
			this.model = model;
			setPreferredSize(new Dimension(980, 520));
			// initialize from model
			refreshFromModel();
		}

		void refreshFromModel() {
			int[] snap = model.getSnapshot();
			for (int i = 0; i < capacity; i++) data[i] = snap[i];
			length = model.getLength();
			pointerI = pointerJ = targetIdx = highlightA = -1;
			repaintOnEDT();
		}

	void randomize() { /* migrated to model */ }

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			int cellW = Math.max(40, getWidth() / capacity);
			int cellH = 60;
			int top = 120;
			g.setFont(getFont().deriveFont(Font.BOLD, 12f));

			for (int i = 0; i < capacity; i++) {
				int x = i * cellW + 8;
				int y = top;
				Color fill = (i < length) ? new Color(0, 123, 255, 170) : new Color(230, 230, 230);
				if (i == highlightA) fill = new Color(255, 193, 7);
				if (i == targetIdx) fill = new Color(40, 167, 69);
				g.setColor(fill);
				g.fillRect(x, y, cellW - 12, cellH);
				g.setColor(Color.DARK_GRAY);
				g.drawRect(x, y, cellW - 12, cellH);
				if (i < length) {
					g.setColor(Color.BLACK);
					g.drawString(String.valueOf(data[i]), x + 10, y + 36);
				}
				g.setColor(Color.GRAY);
				g.drawString("[" + i + "]", x + 10, y + cellH + 16);
			}

			if (pointerI >= 0) drawPointer(g, pointerI * cellW + cellW / 2, top - 30, "i");
			if (pointerJ >= 0) drawPointer(g, pointerJ * cellW + cellW / 2, top - 15, "j");
			if (targetIdx >= 0) drawPointer(g, targetIdx * cellW + cellW / 2, top - 45, "target");

			g.setColor(Color.BLACK);
			g.drawString("Length = " + length + " / " + capacity, 12, 24);
		}

		private void drawPointer(Graphics g, int centerX, int y, String label) {
			g.setColor(Color.BLACK);
			int[] xs = { centerX - 6, centerX + 6, centerX };
			int[] ys = { y, y, y + 8 };
			g.fillPolygon(xs, ys, 3);
			g.drawString(label, centerX - 14, y - 2);
		}

		private void repaintOnEDT() { if (SwingUtilities.isEventDispatchThread()) repaint(); else SwingUtilities.invokeLater(this::repaint); }
		private void stepDelay(IntSupplier d) { try { Thread.sleep(d.getAsInt()); } catch (InterruptedException ignored) {} }

		void push(int value, IntSupplier d) {
			if (length >= capacity) { status.accept("Cannot push: array is full"); return; }
			targetIdx = length; highlightA = targetIdx; status.accept("Push value " + value + " at end index " + targetIdx);
			repaintOnEDT(); stepDelay(d);
			data[length] = value; length++;
			repaintOnEDT(); stepDelay(d);
			highlightA = targetIdx = -1; repaintOnEDT();
		}

		void pop(IntSupplier d) {
			if (length <= 0) { status.accept("Cannot pop: array is empty"); return; }
			int idx = length - 1; highlightA = idx; status.accept("Pop value at end index " + idx);
			repaintOnEDT(); stepDelay(d);
			length--; data[length] = 0;
			repaintOnEDT(); stepDelay(d);
			highlightA = -1; repaintOnEDT();
		}

		void unshift(int value, IntSupplier d) {
			if (length >= capacity) { status.accept("Cannot unshift: array is full"); return; }
			status.accept("Unshift: make room at front by shifting right");
			for (int i = length - 1; i >= 0; i--) {
				pointerI = i; pointerJ = i + 1; highlightA = i; repaintOnEDT(); stepDelay(d);
				data[i + 1] = data[i]; repaintOnEDT(); stepDelay(d);
			}
			data[0] = value; length++;
			pointerI = pointerJ = -1; highlightA = 0; targetIdx = 0; status.accept("Place value at index 0");
			repaintOnEDT(); stepDelay(d);
			highlightA = targetIdx = -1; repaintOnEDT();
		}

		void shift(IntSupplier d) {
			if (length <= 0) { status.accept("Cannot shift: array is empty"); return; }
			status.accept("Shift: remove front element and shift left");
			for (int i = 1; i < length; i++) {
				pointerI = i; pointerJ = i - 1; highlightA = i; repaintOnEDT(); stepDelay(d);
				data[i - 1] = data[i]; repaintOnEDT(); stepDelay(d);
			}
			length--; data[length] = 0;
			pointerI = pointerJ = -1; highlightA = -1; repaintOnEDT();
		}

		void insert(int index, int value, IntSupplier d) {
			if (index < 0 || index > length) { status.accept("Insert: index out of bounds"); return; }
			if (length >= capacity) { status.accept("Insert: array is full"); return; }
			status.accept("Insert: shift elements right from index " + index);
			for (int i = length - 1; i >= index; i--) {
				pointerI = i; pointerJ = i + 1; highlightA = i; targetIdx = index; repaintOnEDT(); stepDelay(d);
				data[i + 1] = data[i]; repaintOnEDT(); stepDelay(d);
			}
			data[index] = value; length++;
			highlightA = index; targetIdx = index; status.accept("Place value at index " + index);
			repaintOnEDT(); stepDelay(d);
			pointerI = pointerJ = -1; highlightA = targetIdx = -1; repaintOnEDT();
		}

		void delete(int index, IntSupplier d) {
			if (index < 0 || index >= length) { status.accept("Delete: index out of bounds"); return; }
			status.accept("Delete: shift elements left from index " + index);
			for (int i = index + 1; i < length; i++) {
				pointerI = i; pointerJ = i - 1; highlightA = i; targetIdx = index; repaintOnEDT(); stepDelay(d);
				data[i - 1] = data[i]; repaintOnEDT(); stepDelay(d);
			}
			length--; data[length] = 0;
			pointerI = pointerJ = -1; highlightA = targetIdx = -1; repaintOnEDT();
		}

		void update(int index, int value, IntSupplier d) {
			if (index < 0 || index >= length) { status.accept("Update: index out of bounds"); return; }
			status.accept("Update: set a[" + index + "] = " + value);
			highlightA = index; targetIdx = index; repaintOnEDT(); stepDelay(d);
			data[index] = value; repaintOnEDT(); stepDelay(d);
			highlightA = targetIdx = -1; repaintOnEDT();
		}

		// New operations
		void linearSearch(int target, IntSupplier d) {
			status.accept("Linear Search for " + target);
			pointerI = -1; targetIdx = -1; highlightA = -1;
			for (int i = 0; i < length; i++) {
				pointerI = i; highlightA = i; repaintOnEDT(); stepDelay(d);
				if (data[i] == target) { targetIdx = i; status.accept("Found at index " + i); repaintOnEDT(); stepDelay(d); break; }
			}
			if (targetIdx == -1) status.accept("Not found");
			pointerI = highlightA = -1; repaintOnEDT();
		}

		void binarySearch(int target, IntSupplier d) {
			if (length <= 0) { status.accept("Binary Search: array is empty"); return; }
			// check sorted ascending
			for (int i = 0; i < length - 1; i++) if (data[i] > data[i + 1]) { status.accept("Binary Search requires sorted array"); return; }
			status.accept("Binary Search for " + target);
			int l = 0, r = length - 1;
			pointerI = pointerJ = -1; targetIdx = -1; highlightA = -1;
			while (l <= r) {
				pointerI = l; pointerJ = r; int mid = (l + r) / 2; highlightA = mid; repaintOnEDT(); stepDelay(d);
				if (data[mid] == target) { targetIdx = mid; status.accept("Found at index " + mid); repaintOnEDT(); break; }
				else if (data[mid] < target) { l = mid + 1; status.accept("Search right half"); }
				else { r = mid - 1; status.accept("Search left half"); }
			}
			if (targetIdx == -1) status.accept("Not found");
			pointerI = pointerJ = highlightA = -1; repaintOnEDT();
		}

		void findMin(IntSupplier d) {
			if (length <= 0) { status.accept("Find Min: array is empty"); return; }
			status.accept("Finding minimum value");
			int minIdx = 0; highlightA = 0; repaintOnEDT(); stepDelay(d);
			for (int i = 1; i < length; i++) {
				pointerI = i; repaintOnEDT(); stepDelay(d);
				if (data[i] < data[minIdx]) { minIdx = i; highlightA = minIdx; status.accept("New min at " + minIdx); repaintOnEDT(); stepDelay(d); }
			}
			status.accept("Min at index " + minIdx + " = " + data[minIdx]);
			pointerI = -1; highlightA = -1; repaintOnEDT();
		}

		void findMax(IntSupplier d) {
			if (length <= 0) { status.accept("Find Max: array is empty"); return; }
			status.accept("Finding maximum value");
			int maxIdx = 0; highlightA = 0; repaintOnEDT(); stepDelay(d);
			for (int i = 1; i < length; i++) {
				pointerI = i; repaintOnEDT(); stepDelay(d);
				if (data[i] > data[maxIdx]) { maxIdx = i; highlightA = maxIdx; status.accept("New max at " + maxIdx); repaintOnEDT(); stepDelay(d); }
			}
			status.accept("Max at index " + maxIdx + " = " + data[maxIdx]);
			pointerI = -1; highlightA = -1; repaintOnEDT();
		}

		void sum(IntSupplier d) {
			if (length <= 0) { status.accept("Sum: array is empty"); return; }
			status.accept("Computing sum");
			int s = 0; for (int i = 0; i < length; i++) { pointerI = i; highlightA = i; repaintOnEDT(); stepDelay(d); s += data[i]; }
			status.accept("Sum = " + s);
			pointerI = highlightA = -1; repaintOnEDT();
		}

		void average(IntSupplier d) {
			if (length <= 0) { status.accept("Average: array is empty"); return; }
			status.accept("Computing average");
			int s = 0; for (int i = 0; i < length; i++) { pointerI = i; highlightA = i; repaintOnEDT(); stepDelay(d); s += data[i]; }
			double avg = (double) s / length; status.accept("Average = " + String.format("%.2f", avg));
			pointerI = highlightA = -1; repaintOnEDT();
		}

		void reverse(IntSupplier d) {
			if (length <= 1) { status.accept("Reverse: nothing to do"); return; }
			status.accept("Reversing array in-place");
			int i = 0, j = length - 1;
			while (i < j) {
				pointerI = i; pointerJ = j; highlightA = i; targetIdx = j; repaintOnEDT(); stepDelay(d);
				int t = data[i]; data[i] = data[j]; data[j] = t; repaintOnEDT(); stepDelay(d);
				i++; j--;
			}
			pointerI = pointerJ = highlightA = targetIdx = -1; repaintOnEDT(); status.accept("Reversed");
		}

		void rotateLeft(int steps, IntSupplier d) {
			if (length <= 1) { status.accept("Rotate Left: nothing to do"); return; }
			steps = steps % length; status.accept("Rotate left by " + steps + " steps");
			for (int s = 0; s < steps; s++) {
				int first = data[0];
				for (int i = 1; i < length; i++) {
					pointerI = i; pointerJ = i - 1; highlightA = i; repaintOnEDT(); stepDelay(d);
					data[i - 1] = data[i]; repaintOnEDT(); stepDelay(d);
				}
				data[length - 1] = first; repaintOnEDT(); stepDelay(d);
			}
			pointerI = pointerJ = highlightA = -1; repaintOnEDT(); status.accept("Rotation complete");
		}

		void rotateRight(int steps, IntSupplier d) {
			if (length <= 1) { status.accept("Rotate Right: nothing to do"); return; }
			steps = steps % length; status.accept("Rotate right by " + steps + " steps");
			for (int s = 0; s < steps; s++) {
				int last = data[length - 1];
				for (int i = length - 1; i > 0; i--) {
					pointerI = i; pointerJ = i - 1; highlightA = i; repaintOnEDT(); stepDelay(d);
					data[i] = data[i - 1]; repaintOnEDT(); stepDelay(d);
				}
				data[0] = last; repaintOnEDT(); stepDelay(d);
			}
			pointerI = pointerJ = highlightA = -1; repaintOnEDT(); status.accept("Rotation complete");
		}

		void swap(int i, int j, IntSupplier d) {
			if (i < 0 || i >= length || j < 0 || j >= length) { status.accept("Swap: index out of bounds"); return; }
			if (i == j) { status.accept("Swap: indices are same, nothing to do"); return; }
			status.accept("Swap indices " + i + " and " + j);
			pointerI = i; pointerJ = j; highlightA = i; targetIdx = j; repaintOnEDT(); stepDelay(d);
			int t = data[i]; data[i] = data[j]; data[j] = t; repaintOnEDT(); stepDelay(d);
			pointerI = pointerJ = highlightA = targetIdx = -1; repaintOnEDT();
		}

		void countOccurrences(int value, IntSupplier d) {
			status.accept("Counting occurrences of " + value);
			int cnt = 0; for (int i = 0; i < length; i++) { pointerI = i; highlightA = i; repaintOnEDT(); stepDelay(d); if (data[i] == value) { cnt++; status.accept("Found at " + i + " (count=" + cnt + ")"); } }
			status.accept("Total occurrences = " + cnt);
			pointerI = highlightA = -1; repaintOnEDT();
		}

		void clear(IntSupplier d) {
			status.accept("Clearing array");
			for (int i = 0; i < length; i++) { pointerI = i; highlightA = i; data[i] = 0; repaintOnEDT(); stepDelay(d); }
			length = 0; pointerI = highlightA = -1; repaintOnEDT(); status.accept("Cleared");
		}

		void fill(int value, IntSupplier d) {
			status.accept("Filling array with " + value);
			for (int i = 0; i < capacity; i++) { pointerI = i; highlightA = i; data[i] = value; if (i >= length) length = i + 1; repaintOnEDT(); stepDelay(d); }
			pointerI = highlightA = -1; repaintOnEDT(); status.accept("Filled");
		}
	}

	@FunctionalInterface interface IntSupplier { int getAsInt(); }
}