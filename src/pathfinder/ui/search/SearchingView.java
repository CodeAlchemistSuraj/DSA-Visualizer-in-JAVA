package pathfinder.ui.search;

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
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

public class SearchingView extends JPanel {
	private final JLabel statusLabel = new JLabel("Ready. Randomize, set a Target, then Start.");
	private final SearchingPanel searchingPanel = new SearchingPanel(this::setStatusText);
	private final JComboBox<String> algoCombo = new JComboBox<>(new String[] {
		"Linear Search",
		"Binary Search",
		"Jump Search",
		"Interpolation Search"
	});
	private final JButton startButton = new JButton("Start");
	private final JButton randomizeButton = new JButton("Randomize");
	private final JSlider speedSlider = new JSlider(0, 200, 20);
	private final JSpinner targetSpinner = new JSpinner(new SpinnerNumberModel(50, 0, 999, 1));

	public SearchingView() {
		super(new BorderLayout());
		JPanel controls = new JPanel();
		controls.add(new JLabel("Algorithm:"));
		controls.add(algoCombo);
		controls.add(new JLabel("Target:"));
		controls.add(targetSpinner);
		controls.add(startButton);
		controls.add(randomizeButton);
		controls.add(new JLabel("Speed:"));
		speedSlider.setInverted(true);
		controls.add(speedSlider);
		add(controls, BorderLayout.NORTH);
		add(searchingPanel, BorderLayout.CENTER);

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
		legend.add(coloredBox(new Color(255, 193, 7), "Current index / probe"));
		legend.add(coloredBox(new Color(40, 167, 69), "Midpoint"));
		legend.add(coloredBox(new Color(0, 123, 255, 90), "Active window (low..high)"));
		legend.add(new JLabel("Pointers: low, high, mid shown above bars"));
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
		randomizeButton.addActionListener(e -> { searchingPanel.randomize(); setStatusText("Array randomized. Choose algorithm, set target, press Start."); });
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
		int target = (Integer) targetSpinner.getValue();
		if (a == null) return;
		switch (a) {
			case "Linear Search": searchingPanel.linearSearch(target, this::delay); break;
			case "Binary Search": searchingPanel.binarySearch(target, this::delay); break;
			case "Jump Search": searchingPanel.jumpSearch(target, this::delay); break;
			case "Interpolation Search": searchingPanel.interpolationSearch(target, this::delay); break;
		}
	}

	static class SearchingPanel extends JPanel {
		private final List<Integer> data = new ArrayList<>();
		private int highlight = -1;
		private int low = -1, high = -1, mid = -1;
		private final Random rnd = new Random();
		private final Consumer<String> status;

		SearchingPanel(Consumer<String> status) {
			this.status = status;
			setPreferredSize(new Dimension(900, 500));
			randomize();
		}

		void randomize() {
			data.clear();
			int n = 80;
			int val = 5;
			for (int i = 0; i < n; i++) { val += rnd.nextInt(8); data.add(val); }
			highlight = -1; low = -1; high = -1; mid = -1;
			repaintOnEDT();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			int width = getWidth();
			int n = data.size();
			int barW = Math.max(2, width / Math.max(1, n));
			if (low >= 0 && high >= 0) {
				g.setColor(new Color(0, 123, 255, 40));
				int x = low * barW;
				int w = (high - low + 1) * barW;
				g.fillRect(x, 0, w, getHeight());
			}
			for (int i = 0; i < n; i++) {
				int h = Math.min(400, data.get(i) * 2);
				int x = i * barW;
				int y = getHeight() - h - 30;
				Color color = new Color(0, 123, 255, 170);
				if (i == highlight) color = new Color(255, 193, 7);
				if (i == mid) color = new Color(40, 167, 69);
				g.setColor(color);
				g.fillRect(x, y, barW - 1, h);
				if (i == highlight || i == mid) {
					g.setColor(Color.BLACK);
					g.drawString(String.valueOf(data.get(i)), x + 2, y - 4);
				}
			}
			if (low >= 0) drawPointer(g, low * barW + barW / 2, 10, "low", new Color(0, 0, 0));
			if (high >= 0) drawPointer(g, high * barW + barW / 2, 22, "high", new Color(120, 0, 0));
			if (mid >= 0) drawPointer(g, mid * barW + barW / 2, 34, "mid", new Color(0, 120, 0));
		}

		private void drawPointer(Graphics g, int centerX, int y, String label, Color color) {
			g.setColor(color);
			int[] xs = { centerX - 6, centerX + 6, centerX };
			int[] ys = { y, y, y + 8 };
			g.fillPolygon(xs, ys, 3);
			g.drawString(label, centerX - 10, y - 2);
		}

		private void repaintOnEDT() { if (SwingUtilities.isEventDispatchThread()) repaint(); else SwingUtilities.invokeLater(this::repaint); }
		private void stepDelay(IntSupplier d) { try { Thread.sleep(d.getAsInt()); } catch (InterruptedException ignored) {} }

		void linearSearch(int target, IntSupplier d) {
			status.accept("Linear: scan each element, stop if equals target " + target);
			for (int i = 0; i < data.size(); i++) {
				highlight = i; repaintOnEDT(); stepDelay(d);
				if (data.get(i) == target) { status.accept("Found at index " + i); return; }
			}
			highlight = -1; repaintOnEDT(); status.accept("Not found");
		}

		void binarySearch(int target, IntSupplier d) {
			status.accept("Binary: repeatedly split the sorted array and check the middle");
			int l = 0, r = data.size() - 1;
			while (l <= r) {
				low = l; high = r; mid = (l + r) / 2; highlight = mid; repaintOnEDT(); stepDelay(d);
				int v = data.get(mid);
				status.accept("Check mid=" + mid + " (value " + v + ") vs target " + target);
				if (v == target) { status.accept("Found at index " + mid); return; }
				else if (v < target) { l = mid + 1; status.accept("Target is larger; search right half"); }
				else { r = mid - 1; status.accept("Target is smaller; search left half"); }
			}
			low = high = mid = -1; highlight = -1; repaintOnEDT(); status.accept("Not found");
		}

		void jumpSearch(int target, IntSupplier d) {
			status.accept("Jump: jump by sqrt(n) to find a block, then linear scan");
			int n = data.size();
			int step = (int) Math.sqrt(n);
			int prev = 0;
			while (prev < n && data.get(Math.min(step, n) - 1) < target) {
				low = prev; high = Math.min(step, n) - 1; repaintOnEDT(); stepDelay(d);
				status.accept("Jump to index " + high + "; still less than target");
				prev = step; step += (int) Math.sqrt(n);
			}
			while (prev < n && data.get(prev) < target) { highlight = prev; repaintOnEDT(); stepDelay(d); prev++; }
			if (prev < n && data.get(prev) == target) { highlight = prev; repaintOnEDT(); status.accept("Found at index " + prev); }
			low = high = -1;
		}

		void interpolationSearch(int target, IntSupplier d) {
			status.accept("Interpolation: estimate position proportional to values");
			int l = 0, r = data.size() - 1;
			while (l <= r && target >= data.get(l) && target <= data.get(r)) {
				if (l == r) { highlight = l; repaintOnEDT(); status.accept("Found at index " + l); return; }
				int pos = l + (int)((long)(target - data.get(l)) * (r - l) / Math.max(1, (data.get(r) - data.get(l))));
				low = l; high = r; mid = pos; highlight = pos; repaintOnEDT(); stepDelay(d);
				int v = data.get(pos);
				status.accept("Probe at pos=" + pos + " (value " + v + ")");
				if (v == target) { status.accept("Found at index " + pos); return; }
				if (v < target) { l = pos + 1; status.accept("Target larger; move right"); }
				else { r = pos - 1; status.accept("Target smaller; move left"); }
			}
			low = high = mid = -1; highlight = -1; repaintOnEDT(); status.accept("Not found");
		}
	}

	@FunctionalInterface interface IntSupplier { int getAsInt(); }
} 