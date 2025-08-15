package pathfinder.ui.sort;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

public class SortingView extends JPanel {
	private final JLabel statusLabel = new JLabel("Ready. Click Shuffle, then Start.");
	private final SortingPanel sortingPanel = new SortingPanel(this::setStatusText);
	private final JComboBox<String> algoCombo = new JComboBox<>(new String[] {
		"Bubble Sort",
		"Insertion Sort",
		"Selection Sort",
		"Merge Sort",
		"Quick Sort",
		"Heap Sort"
	});
	private final JButton startButton = new JButton("Start");
	private final JButton shuffleButton = new JButton("Shuffle");
	private final JSlider speedSlider = new JSlider(0, 200, 20);

	public SortingView() {
		super(new BorderLayout());
		JPanel controls = new JPanel();
		controls.add(new JLabel("Algorithm:"));
		controls.add(algoCombo);
		controls.add(startButton);
		controls.add(shuffleButton);
		controls.add(new JLabel("Speed:"));
		speedSlider.setInverted(true);
		controls.add(speedSlider);
		add(controls, BorderLayout.NORTH);
		add(sortingPanel, BorderLayout.CENTER);

		JPanel bottom = new JPanel(new BorderLayout());
		bottom.setBorder(BorderFactory.createEmptyBorder(4, 8, 6, 8));
		bottom.add(buildLegend(), BorderLayout.NORTH);
		bottom.add(statusLabel, BorderLayout.SOUTH);
		add(bottom, BorderLayout.SOUTH);

		wire();
	}

	private JPanel buildLegend() {
		JPanel legend = new JPanel();
		legend.add(coloredBox(new Color(0, 123, 255, 170), "Array element"));
		legend.add(coloredBox(new Color(255, 193, 7), "Compared"));
		legend.add(coloredBox(new Color(40, 167, 69), "Pivot / Key / Min"));
		legend.add(coloredBox(new Color(0, 123, 255, 40), "Active range"));
		legend.add(new JLabel("Pointers: i, j, pivot shown above bars"));
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
		shuffleButton.addActionListener(e -> { sortingPanel.shuffle(); setStatusText("Shuffled. Choose an algorithm and press Start."); });
		startButton.addActionListener(e -> {
			startButton.setEnabled(false);
			new Thread(() -> {
				try {
					setStatusText("Running " + algoCombo.getSelectedItem() + "...");
					runSelected();
					setStatusText("Done.");
				} finally {
					SwingUtilities.invokeLater(() -> startButton.setEnabled(true));
				}
			}).start();
		});
	}

	private int delay() { return Math.max(1, speedSlider.getMaximum() - speedSlider.getValue() + 1); }
	private void setStatusText(String t) { SwingUtilities.invokeLater(() -> statusLabel.setText(t)); }

	private void runSelected() {
		String a = (String) algoCombo.getSelectedItem();
		if (a == null) return;
		switch (a) {
			case "Bubble Sort": sortingPanel.bubbleSort(this::delay); break;
			case "Insertion Sort": sortingPanel.insertionSort(this::delay); break;
			case "Selection Sort": sortingPanel.selectionSort(this::delay); break;
			case "Merge Sort": sortingPanel.mergeSort(this::delay); break;
			case "Quick Sort": sortingPanel.quickSort(this::delay); break;
			case "Heap Sort": sortingPanel.heapSort(this::delay); break;
		}
	}

	static class SortingPanel extends JPanel {
		private final List<Integer> data = new ArrayList<>();
		private int highlightA = -1, highlightB = -1;
		private int pointerI = -1, pointerJ = -1, pointerPivot = -1, rangeL = -1, rangeR = -1, currentMin = -1;
		private final Random rnd = new Random();
		private final Consumer<String> status;

		SortingPanel(Consumer<String> status) {
			this.status = status;
			setPreferredSize(new Dimension(900, 500));
			shuffle();
		}

		void shuffle() {
			data.clear();
			int n = 80;
			for (int i = 0; i < n; i++) data.add(rnd.nextInt(380) + 40);
			highlightA = highlightB = -1;
			pointerI = pointerJ = pointerPivot = -1; rangeL = rangeR = -1; currentMin = -1;
			repaintOnEDT();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			int width = getWidth();
			int n = data.size();
			int barW = Math.max(2, width / Math.max(1, n));

			if (rangeL >= 0 && rangeR >= 0) {
				g.setColor(new Color(0, 123, 255, 40));
				int x = rangeL * barW;
				int w = (rangeR - rangeL + 1) * barW;
				g.fillRect(x, 0, w, getHeight());
			}

			for (int i = 0; i < n; i++) {
				int h = data.get(i);
				int x = i * barW;
				int y = getHeight() - h - 30;
				Color color = new Color(0, 123, 255, 170);
				if (i == highlightA || i == highlightB) color = new Color(255, 193, 7);
				if (i == currentMin || i == pointerPivot) color = new Color(40, 167, 69);
				g.setColor(color);
				g.fillRect(x, y, barW - 1, h);
				if (i == highlightA || i == highlightB || i == currentMin || i == pointerPivot) {
					g.setColor(Color.BLACK);
					g.drawString(String.valueOf(h), x + 2, y - 4);
				}
			}

			if (pointerI >= 0) drawPointer(g, pointerI * barW + barW / 2, 10, "i", new Color(0, 0, 0));
			if (pointerJ >= 0) drawPointer(g, pointerJ * barW + barW / 2, 22, "j", new Color(120, 0, 0));
			if (pointerPivot >= 0) drawPointer(g, pointerPivot * barW + barW / 2, 34, "pivot", new Color(0, 120, 0));
		}

		private void drawPointer(Graphics g, int centerX, int y, String label, Color color) {
			g.setColor(color);
			int[] xs = { centerX - 6, centerX + 6, centerX };
			int[] ys = { y, y, y + 8 };
			g.fillPolygon(xs, ys, 3);
			g.drawString(label, centerX - 10, y - 2);
		}

		private void swap(int i, int j) { int tmp = data.get(i); data.set(i, data.get(j)); data.set(j, tmp); }
		private void stepDelay(IntSupplier delaySupplier) { try { Thread.sleep(delaySupplier.getAsInt()); } catch (InterruptedException ignored) {} }
		private void setHL(int a, int b) { highlightA = a; highlightB = b; repaintOnEDT(); }
		private void repaintOnEDT() { if (SwingUtilities.isEventDispatchThread()) repaint(); else SwingUtilities.invokeLater(this::repaint); }
		private void setPointers(Integer i, Integer j, Integer pivot, Integer l, Integer r) {
			pointerI = i == null ? -1 : i;
			pointerJ = j == null ? -1 : j;
			pointerPivot = pivot == null ? -1 : pivot;
			rangeL = l == null ? -1 : l;
			rangeR = r == null ? -1 : r;
			repaintOnEDT();
		}

		void bubbleSort(IntSupplier d) {
			int n = data.size();
			for (int i = 0; i < n - 1; i++) {
				setPointers(i, -1, null, 0, n - i - 1);
				for (int j = 0; j < n - i - 1; j++) {
					setPointers(i, j, null, 0, n - i - 1);
					setHL(j, j + 1);
					status.accept("Comparing a[" + j + "]=" + data.get(j) + " and a[" + (j + 1) + "]=" + data.get(j + 1));
					if (data.get(j) > data.get(j + 1)) {
						status.accept("Swap because left > right");
						swap(j, j + 1);
						repaintOnEDT();
					}
					stepDelay(d);
				}
			}
			status.accept("Array is sorted.");
			setHL(-1, -1); setPointers(-1, -1, null, -1, -1);
		}

		void insertionSort(IntSupplier d) {
			for (int i = 1; i < data.size(); i++) {
				int key = data.get(i);
				pointerPivot = i; pointerI = i; rangeL = 0; rangeR = i; repaintOnEDT();
				status.accept("Pick key = a[" + i + "]=" + key + "; insert it into the sorted left part.");
				int j = i - 1;
				while (j >= 0 && data.get(j) > key) {
					setPointers(j, j + 1, i, 0, i);
					setHL(j, j + 1);
					status.accept("Shift a[" + j + "]=" + data.get(j) + " right because > key");
					data.set(j + 1, data.get(j));
					repaintOnEDT();
					stepDelay(d);
					j--;
				}
				data.set(j + 1, key);
				repaintOnEDT();
				status.accept("Place key at position " + (j + 1));
				stepDelay(d);
			}
			status.accept("Array is sorted.");
			setHL(-1, -1); setPointers(-1, -1, null, -1, -1);
		}

		void selectionSort(IntSupplier d) {
			for (int i = 0; i < data.size() - 1; i++) {
				int min = i; currentMin = min; setPointers(i, -1, null, i, data.size() - 1);
				status.accept("Find minimum from i=" + i + " to end.");
				for (int j = i + 1; j < data.size(); j++) {
					setHL(min, j); pointerJ = j; repaintOnEDT();
					if (data.get(j) < data.get(min)) { min = j; currentMin = min; status.accept("New minimum at index " + min); }
					stepDelay(d);
				}
				swap(i, min); status.accept("Swap minimum into position i=" + i);
				repaintOnEDT(); stepDelay(d);
			}
			status.accept("Array is sorted.");
			setHL(-1, -1); currentMin = -1; setPointers(-1, -1, null, -1, -1);
		}

		void mergeSort(IntSupplier d) { mergeSortRange(0, data.size() - 1, d); status.accept("Array is sorted."); setHL(-1, -1); setPointers(-1, -1, null, -1, -1); }
		private void mergeSortRange(int l, int r, IntSupplier d) {
			if (l >= r) return;
			int m = (l + r) / 2;
			mergeSortRange(l, m, d);
			mergeSortRange(m + 1, r, d);
			merge(l, m, r, d);
		}
		private void merge(int l, int m, int r, IntSupplier d) {
			status.accept("Merge two sorted halves: [" + l + "," + m + "] and [" + (m + 1) + "," + r + "]");
			rangeL = l; rangeR = r; repaintOnEDT();
			int n1 = m - l + 1, n2 = r - m;
			int[] L = new int[n1]; int[] R = new int[n2];
			for (int i = 0; i < n1; i++) L[i] = data.get(l + i);
			for (int j = 0; j < n2; j++) R[j] = data.get(m + 1 + j);
			int i = 0, j = 0, k = l;
			while (i < n1 && j < n2) {
				setHL(l + i, m + 1 + j);
				status.accept("Compare L[" + i + "]=" + L[i] + " and R[" + j + "]=" + R[j]);
				if (L[i] <= R[j]) { data.set(k++, L[i++]); } else { data.set(k++, R[j++]); }
				repaintOnEDT(); stepDelay(d);
			}
			while (i < n1) { data.set(k++, L[i++]); repaintOnEDT(); stepDelay(d); }
			while (j < n2) { data.set(k++, R[j++]); repaintOnEDT(); stepDelay(d); }
		}

		void quickSort(IntSupplier d) { quickSortRange(0, data.size() - 1, d); status.accept("Array is sorted."); setHL(-1, -1); setPointers(-1, -1, null, -1, -1); }
		private void quickSortRange(int l, int r, IntSupplier d) {
			if (l >= r) return;
			int p = partition(l, r, d);
			quickSortRange(l, p - 1, d);
			quickSortRange(p + 1, r, d);
		}
		private int partition(int l, int r, IntSupplier d) {
			pointerPivot = r; rangeL = l; rangeR = r; repaintOnEDT();
			status.accept("Partition: choose pivot a[" + r + "]=" + data.get(r));
			int pivot = data.get(r); int i = l;
			for (int j = l; j < r; j++) {
				setPointers(i, j, r, l, r);
				setHL(j, r);
				status.accept("Compare a[" + j + "]=" + data.get(j) + " with pivot " + pivot);
				if (data.get(j) < pivot) { swap(i, j); status.accept("Swap a[" + i + "] and a[" + j + "] (move smaller left)"); i++; repaintOnEDT(); }
				repaintOnEDT(); stepDelay(d);
			}
			swap(i, r); status.accept("Place pivot at index " + i); repaintOnEDT(); stepDelay(d);
			return i;
		}

		void heapSort(IntSupplier d) {
			int n = data.size();
			status.accept("Build max-heap.");
			for (int i = n / 2 - 1; i >= 0; i--) { pointerI = i; repaintOnEDT(); heapify(n, i, d); }
			status.accept("Extract max and rebuild heap.");
			for (int i = n - 1; i > 0; i--) { swap(0, i); status.accept("Move max to end (index " + i + ")"); repaintOnEDT(); stepDelay(d); heapify(i, 0, d); }
			setHL(-1, -1); setPointers(-1, -1, null, -1, -1);
		}
		private void heapify(int n, int i, IntSupplier d) {
			int largest = i; int l = 2 * i + 1; int r = 2 * i + 2;
			pointerI = i; pointerJ = l; pointerPivot = r; repaintOnEDT();
			if (l < n && data.get(l) > data.get(largest)) largest = l;
			if (r < n && data.get(r) > data.get(largest)) largest = r;
			status.accept("Heapify at i=" + i + ": put largest of (i, left, right) at i");
			if (largest != i) { swap(i, largest); repaintOnEDT(); stepDelay(d); heapify(n, largest, d); }
		}
	}

	@FunctionalInterface
	interface IntSupplier { int getAsInt(); }
} 