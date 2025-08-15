package pathfinder.ui.trees;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

public class TreeView extends JPanel {
	private final JLabel statusLabel = new JLabel("Ready. Insert/Search/Traverse.");
	private final TreePanel treePanel = new TreePanel();
	private final JButton randomizeButton = new JButton("Randomize");
	private final JButton insertButton = new JButton("Insert");
	private final JButton searchButton = new JButton("Search");
	private final JButton inorderButton = new JButton("Inorder");
	private final JButton preorderButton = new JButton("Preorder");
	private final JButton postorderButton = new JButton("Postorder");
	private final JSpinner valueSpinner = new JSpinner(new SpinnerNumberModel(50, -999, 999, 1));
	private final JSlider speedSlider = new JSlider(0, 200, 20);

	public TreeView() {
		super(new BorderLayout());
		JPanel controls = new JPanel();
		controls.add(new JLabel("Value:"));
		controls.add(valueSpinner);
		controls.add(insertButton);
		controls.add(searchButton);
		controls.add(inorderButton);
		controls.add(preorderButton);
		controls.add(postorderButton);
		controls.add(randomizeButton);
		controls.add(new JLabel("Speed:"));
		speedSlider.setInverted(true);
		controls.add(speedSlider);
		add(controls, BorderLayout.NORTH);
		add(treePanel, BorderLayout.CENTER);

		JPanel bottom = new JPanel(new BorderLayout());
		bottom.setBorder(BorderFactory.createEmptyBorder(4, 8, 6, 8));
		bottom.add(new JLabel("Legend: yellow = current node, green = found/highlight. Edges show parent-child."), BorderLayout.NORTH);
		bottom.add(statusLabel, BorderLayout.SOUTH);
		add(bottom, BorderLayout.SOUTH);

		wire();
	}

	private void wire() {
		randomizeButton.addActionListener(e -> { treePanel.randomize(); setStatus("Randomized tree."); });
		insertButton.addActionListener(e -> runAsync(() -> treePanel.insert((Integer) valueSpinner.getValue(), this::delay)));
		searchButton.addActionListener(e -> runAsync(() -> treePanel.search((Integer) valueSpinner.getValue(), this::delay)));
		inorderButton.addActionListener(e -> runAsync(() -> treePanel.traverse("in", this::delay)));
		preorderButton.addActionListener(e -> runAsync(() -> treePanel.traverse("pre", this::delay)));
		postorderButton.addActionListener(e -> runAsync(() -> treePanel.traverse("post", this::delay)));
	}

	private void runAsync(Runnable r) { new Thread(r).start(); }
	private int delay() { return Math.max(1, speedSlider.getMaximum() - speedSlider.getValue() + 1); }
	private void setStatus(String s) { SwingUtilities.invokeLater(() -> statusLabel.setText(s)); }

	class TreePanel extends JPanel {
		private Node root;
		private Node current;
		private final Random rnd = new Random();
		private int nodeW = 36, nodeH = 28, levelGap = 70;

		TreePanel() { setPreferredSize(new Dimension(980, 560)); randomize(); }

		void randomize() {
			root = null;
			for (int i = 0; i < 8; i++) insertInternal(rnd.nextInt(90) + 10);
			repaintOnEDT();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			drawTree(g, root);
		}

		private void drawTree(Graphics g, Node n) {
			if (n == null) return;
			int x = toX(n), y = toY(n);
			if (n.left != null) { drawEdge(g, x, y, toX(n.left), toY(n.left)); drawTree(g, n.left); }
			if (n.right != null) { drawEdge(g, x, y, toX(n.right), toY(n.right)); drawTree(g, n.right); }
			drawNode(g, n);
		}

		private void drawEdge(Graphics g, int ax, int ay, int bx, int by) {
			g.setColor(new Color(200, 200, 200));
			g.drawLine(ax, ay, bx, by);
		}

		private void drawNode(Graphics g, Node n) {
			int x = toX(n) - nodeW / 2;
			int y = toY(n) - nodeH / 2;
			Color fill = n == current ? new Color(255, 193, 7) : new Color(0, 123, 255, 170);
			if (n.found) fill = new Color(40, 167, 69);
			g.setColor(fill);
			g.fillOval(x, y, nodeW, nodeH);
			g.setColor(Color.DARK_GRAY);
			g.drawOval(x, y, nodeW, nodeH);
			g.setColor(Color.BLACK);
			g.setFont(getFont().deriveFont(Font.BOLD, 12f));
			g.drawString(String.valueOf(n.val), x + nodeW / 2 - 8, y + nodeH / 2 + 5);
		}

		private int toX(Node n) { return (int) (getWidth() / 2 + n.x * 40); }
		private int toY(Node n) { return 60 + n.depth * levelGap; }

		private void layoutTree() { layoutDfs(root, 0, 0); }
		private void layoutDfs(Node n, int depth, int x) { if (n == null) return; n.depth = depth; n.x = x; layoutDfs(n.left, depth + 1, x - (1 << Math.max(0, 3 - depth))); layoutDfs(n.right, depth + 1, x + (1 << Math.max(0, 3 - depth))); }

		void insert(int val, IntSupplier d) { insertInternal(val); repaintOnEDT(); }
		void search(int val, IntSupplier d) {
			setStatus("Search " + val + ": compare and follow left/right");
			Node p = root; current = null; repaintOnEDT(); stepDelay(d);
			while (p != null) {
				current = p; repaintOnEDT(); stepDelay(d);
				if (val == p.val) { p.found = true; repaintOnEDT(); stepDelay(d); setStatus("Found " + val); return; }
				if (val < p.val) { setStatus("Go left (" + val + " < " + p.val + ")"); p = p.left; }
				else { setStatus("Go right (" + val + " > " + p.val + ")"); p = p.right; }
			}
			setStatus("Not found");
		}

		void traverse(String kind, IntSupplier d) {
			List<Node> order = new ArrayList<>();
			if ("pre".equals(kind)) preorder(root, order); else if ("post".equals(kind)) postorder(root, order); else inorder(root, order);
			setStatus(("pre".equals(kind) ? "Preorder" : ("post".equals(kind) ? "Postorder" : "Inorder")) + " traversal");
			for (Node n : order) { current = n; repaintOnEDT(); stepDelay(d); }
			current = null; repaintOnEDT();
		}

		private void insertInternal(int val) {
			if (root == null) { root = new Node(val); layoutTree(); return; }
			Node p = root;
			while (true) {
				if (val < p.val) { if (p.left == null) { p.left = new Node(val); break; } p = p.left; }
				else { if (p.right == null) { p.right = new Node(val); break; } p = p.right; }
			}
			layoutTree();
		}

		private void inorder(Node n, List<Node> out) { if (n == null) return; inorder(n.left, out); out.add(n); inorder(n.right, out); }
		private void preorder(Node n, List<Node> out) { if (n == null) return; out.add(n); preorder(n.left, out); preorder(n.right, out); }
		private void postorder(Node n, List<Node> out) { if (n == null) return; postorder(n.left, out); postorder(n.right, out); out.add(n); }

		private void repaintOnEDT() { if (SwingUtilities.isEventDispatchThread()) repaint(); else SwingUtilities.invokeLater(this::repaint); }
		private void stepDelay(IntSupplier d) { try { Thread.sleep(d.getAsInt()); } catch (InterruptedException ignored) {} }
	}

	static class Node { int val; Node left, right; int depth; int x; boolean found; Node(int v) { this.val = v; } }
	@FunctionalInterface interface IntSupplier { int getAsInt(); }
} 