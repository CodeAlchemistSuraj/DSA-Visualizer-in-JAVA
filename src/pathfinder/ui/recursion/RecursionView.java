package pathfinder.ui.recursion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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

public class RecursionView extends JPanel {
	private final JLabel statusLabel = new JLabel("Ready. Choose a problem and press Start.");
	private final JComboBox<String> problemCombo = new JComboBox<>(new String[] { "Factorial", "Fibonacci" });
	private final JSpinner nSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 10, 1));
	private final JButton startButton = new JButton("Start");
	private final JButton resetButton = new JButton("Reset");
	private final JSlider speedSlider = new JSlider(0, 200, 20);
	private final RecursionPanel panel = new RecursionPanel(this::setStatusText);

	public RecursionView() {
		super(new BorderLayout());
		JPanel controls = new JPanel();
		controls.add(new JLabel("Problem:"));
		controls.add(problemCombo);
		controls.add(new JLabel("n:"));
		controls.add(nSpinner);
		controls.add(startButton);
		controls.add(resetButton);
		controls.add(new JLabel("Speed:"));
		speedSlider.setInverted(true);
		controls.add(speedSlider);
		add(controls, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);

		JPanel bottom = new JPanel(new BorderLayout());
		bottom.setBorder(BorderFactory.createEmptyBorder(4, 8, 6, 8));
		bottom.add(new JLabel("Legend: yellow = currently executing, green = computed/return value. Right side shows Call Stack"), BorderLayout.NORTH);
		bottom.add(statusLabel, BorderLayout.SOUTH);
		add(bottom, BorderLayout.SOUTH);

		wire();
	}

	private void wire() {
		resetButton.addActionListener(e -> { panel.reset(); setStatusText("Reset."); });
		startButton.addActionListener(e -> {
			startButton.setEnabled(false);
			new Thread(() -> {
				try {
					String p = (String) problemCombo.getSelectedItem();
					int n = (Integer) nSpinner.getValue();
					setStatusText("Building recursion...");
					panel.buildProblem(p, n);
					setStatusText("Running " + p + "(" + n + ")");
					panel.runAnimation(this::delay);
					setStatusText("Done.");
				} finally {
					SwingUtilities.invokeLater(() -> startButton.setEnabled(true));
				}
			}).start();
		});
	}

	private int delay() { return Math.max(1, speedSlider.getMaximum() - speedSlider.getValue() + 1); }
	private void setStatusText(String t) { SwingUtilities.invokeLater(() -> statusLabel.setText(t)); }

	static class RecursionPanel extends JPanel {
		private final Consumer<String> status;
		private Node root;
		private final List<Frame> callStack = new ArrayList<>();
		private int width;
		private int nodeW = 70, nodeH = 36, levelGap = 70;

		RecursionPanel(Consumer<String> status) {
			this.status = status;
			setPreferredSize(new Dimension(980, 560));
		}

		void reset() { root = null; callStack.clear(); repaintOnEDT(); }

		void buildProblem(String name, int n) {
			if ("Factorial".equals(name)) root = buildFactorial(n);
			else root = buildFibonacci(n);
			layoutTree(); repaintOnEDT();
		}

		private Node buildFactorial(int n) {
			Node node = new Node("fact(" + n + ")");
			if (n <= 1) { node.value = 1; return node; }
			node.children.add(buildFactorial(n - 1));
			return node;
		}

		private Node buildFibonacci(int n) {
			Node node = new Node("fib(" + n + ")");
			if (n <= 1) { node.value = n; return node; }
			node.children.add(buildFibonacci(n - 1));
			node.children.add(buildFibonacci(n - 2));
			return node;
		}

		private void layoutTree() {
			width = getWidth();
			AtomicInteger xCounter = new AtomicInteger(0);
			assignDepths(root, 0);
			assignPositions(root, xCounter);
		}

		private void assignDepths(Node node, int depth) {
			if (node == null) return;
			node.depth = depth;
			for (Node ch : node.children) assignDepths(ch, depth + 1);
		}

		private void assignPositions(Node node, AtomicInteger xCounter) {
			if (node.children.isEmpty()) {
				node.x = xCounter.getAndIncrement();
			} else {
				for (Node ch : node.children) assignPositions(ch, xCounter);
				node.x = (node.children.get(0).x + node.children.get(node.children.size() - 1).x) / 2.0;
			}
		}

		void runAnimation(IntSupplier d) {
			if (root == null) return;
			callStack.clear();
			int result = execute(root, d);
			status.accept("Result = " + result);
		}

		private int execute(Node node, IntSupplier d) {
			pushFrame(node.label); highlight(node, true); step(d);
			int result;
			if (node.children.isEmpty()) {
				result = node.value;
				status.accept("Base case: return " + result);
			} else {
				int sum = 0;
				for (Node ch : node.children) {
					sum += execute(ch, d);
				}
				if (node.label.startsWith("fact")) result = (int) (extractN(node.label) * (long) sum);
				else result = sum;
				status.accept("Return from " + node.label + " = " + result);
			}
			node.computed = true; node.value = result; highlight(node, false); step(d);
			popFrame(); step(d);
			return result;
		}

		private int extractN(String label) { int s = label.indexOf('(') + 1, e = label.indexOf(')'); return Integer.parseInt(label.substring(s, e)); }

		private void pushFrame(String title) { callStack.add(new Frame(title)); repaintOnEDT(); }
		private void popFrame() { if (!callStack.isEmpty()) callStack.remove(callStack.size() - 1); repaintOnEDT(); }
		private void highlight(Node n, boolean active) { n.active = active; repaintOnEDT(); }
		private void step(IntSupplier d) { try { Thread.sleep(d.getAsInt()); } catch (InterruptedException ignored) {} }
		private void repaintOnEDT() { if (SwingUtilities.isEventDispatchThread()) repaint(); else SwingUtilities.invokeLater(this::repaint); }

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (root == null) return;
			layoutTree();
			drawTree(g, root);
			drawStack(g);
		}

		private void drawTree(Graphics g, Node node) {
			for (Node ch : node.children) drawTree(g, ch);
			for (Node ch : node.children) drawEdge(g, node, ch);
			drawNode(g, node);
		}

		private void drawEdge(Graphics g, Node a, Node b) {
			int ax = toX(a), ay = toY(a);
			int bx = toX(b), by = toY(b);
			g.setColor(new Color(200, 200, 200));
			g.drawLine(ax, ay, bx, by);
		}

		private int toX(Node n) { double spacing = Math.max(nodeW + 10, getWidth() / Math.pow(2, n.depth + 1)); return (int) (n.x * spacing + nodeW); }
		private int toY(Node n) { return 40 + n.depth * levelGap; }

		private void drawNode(Graphics g, Node n) {
			int x = toX(n) - nodeW / 2;
			int y = toY(n) - nodeH / 2;
			Color fill = new Color(0, 123, 255, 130);
			if (n.active) fill = new Color(255, 193, 7);
			if (n.computed) fill = new Color(40, 167, 69);
			g.setColor(fill);
			g.fillRoundRect(x, y, nodeW, nodeH, 10, 10);
			g.setColor(Color.DARK_GRAY);
			g.drawRoundRect(x, y, nodeW, nodeH, 10, 10);
			g.setColor(Color.BLACK);
			g.setFont(getFont().deriveFont(Font.BOLD, 12f));
			String text = n.label + (n.computed ? (" = " + n.value) : "");
			g.drawString(text, x + 6, y + 22);
		}

		private void drawStack(Graphics g) {
			int sx = getWidth() - 220;
			g.setColor(Color.BLACK);
			g.drawString("Call Stack (top)", sx, 24);
			int y = 40;
			for (int i = callStack.size() - 1; i >= 0; i--) {
				Frame f = callStack.get(i);
				g.setColor(new Color(245, 245, 245));
				g.fillRect(sx, y, 200, 28);
				g.setColor(Color.DARK_GRAY);
				g.drawRect(sx, y, 200, 28);
				g.setColor(Color.BLACK);
				g.drawString(f.title, sx + 8, y + 18);
				y += 32;
			}
		}

		static class Node {
			final String label;
			final List<Node> children = new ArrayList<>();
			boolean active = false;
			boolean computed = false;
			double x;
			int depth;
			int value = 0;
			Node(String label) { this.label = label; }
		}

		static class Frame { final String title; Frame(String t) { this.title = t; } }
	}

	@FunctionalInterface interface IntSupplier { int getAsInt(); }
} 