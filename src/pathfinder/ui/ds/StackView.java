package pathfinder.ui.ds;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

public class StackView extends JPanel {
	private final JLabel statusLabel = new JLabel("Ready. Use Push/Pop/Peek.");
	private final StackPanel stackPanel = new StackPanel();
	private final JButton pushButton = new JButton("Push");
	private final JButton popButton = new JButton("Pop");
	private final JButton peekButton = new JButton("Peek");
	private final JButton randomizeButton = new JButton("Randomize");
	private final JSpinner valueSpinner = new JSpinner(new SpinnerNumberModel(42, -999, 999, 1));
	private final JSlider speedSlider = new JSlider(0, 200, 20);

	public StackView() {
		super(new BorderLayout());
		JPanel controls = new JPanel();
		controls.add(new JLabel("Value:"));
		controls.add(valueSpinner);
		controls.add(pushButton);
		controls.add(popButton);
		controls.add(peekButton);
		controls.add(randomizeButton);
		controls.add(new JLabel("Speed:"));
		speedSlider.setInverted(true);
		controls.add(speedSlider);
		add(controls, BorderLayout.NORTH);
		add(stackPanel, BorderLayout.CENTER);

		JPanel bottom = new JPanel(new BorderLayout());
		bottom.setBorder(BorderFactory.createEmptyBorder(4, 8, 6, 8));
		bottom.add(new JLabel("Legend: Top of stack is at the top. Yellow = active element."), BorderLayout.NORTH);
		bottom.add(statusLabel, BorderLayout.SOUTH);
		add(bottom, BorderLayout.SOUTH);

		wire();
	}

	private void wire() {
		pushButton.addActionListener(e -> runAsync(() -> stackPanel.push((Integer) valueSpinner.getValue(), this::delay)));
		popButton.addActionListener(e -> runAsync(() -> stackPanel.pop(this::delay)));
		peekButton.addActionListener(e -> runAsync(() -> stackPanel.peek(this::delay)));
		randomizeButton.addActionListener(e -> { stackPanel.randomize(); setStatus("Randomized."); });
	}

	private void runAsync(Runnable r) {
		setControlsEnabled(false);
		new Thread(() -> {
			try { r.run(); } finally { SwingUtilities.invokeLater(() -> setControlsEnabled(true)); }
		}).start();
	}

	private void setControlsEnabled(boolean enabled) {
		pushButton.setEnabled(enabled);
		popButton.setEnabled(enabled);
		peekButton.setEnabled(enabled);
		randomizeButton.setEnabled(enabled);
	}

	private int delay() { return Math.max(1, speedSlider.getMaximum() - speedSlider.getValue() + 1); }
	private void setStatus(String s) { SwingUtilities.invokeLater(() -> statusLabel.setText(s)); }

	class StackPanel extends JPanel {
		private final int capacity = 10;
		private final Deque<Integer> stack = new ArrayDeque<>();
		private Integer highlight = null;
		private final Random rnd = new Random();

		StackPanel() { setPreferredSize(new Dimension(980, 520)); randomize(); }

		void randomize() {
			stack.clear();
			int n = 3 + rnd.nextInt(4);
			for (int i = 0; i < n; i++) stack.push(rnd.nextInt(90) + 10);
			highlight = null; repaintOnEDT();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			int sx = getWidth() / 2 - 80;
			int boxW = 160, boxH = 42, gap = 6;
			int y = 80;
			g.setFont(getFont().deriveFont(Font.BOLD, 14f));
			g.setColor(Color.BLACK);
			g.drawString("Top", sx + boxW + 16, y - 8);
			int level = 0;
			for (Integer v : stack) {
				Color fill = v.equals(highlight) ? new Color(255, 193, 7) : new Color(0, 123, 255, 170);
				g.setColor(fill);
				g.fillRect(sx, y, boxW, boxH);
				g.setColor(Color.DARK_GRAY);
				g.drawRect(sx, y, boxW, boxH);
				g.setColor(Color.BLACK);
				g.drawString(String.valueOf(v), sx + boxW / 2 - 10, y + 26);
				y += boxH + gap; level++;
			}
			g.setColor(new Color(230, 230, 230));
			for (; level < capacity; level++) {
				g.fillRect(sx, y, boxW, boxH);
				g.setColor(Color.LIGHT_GRAY);
				g.drawRect(sx, y, boxW, boxH);
				g.setColor(new Color(230, 230, 230));
				y += boxH + gap;
			}
		}

		private void repaintOnEDT() { if (SwingUtilities.isEventDispatchThread()) repaint(); else SwingUtilities.invokeLater(this::repaint); }
		private void stepDelay(IntSupplier d) { try { Thread.sleep(d.getAsInt()); } catch (InterruptedException ignored) {} }

		void push(int value, IntSupplier d) {
			if (stack.size() >= capacity) { setStatus("Stack overflow: capacity reached"); return; }
			setStatus("Push " + value);
			highlight = value; repaintOnEDT(); stepDelay(d);
			stack.push(value); repaintOnEDT(); stepDelay(d);
			highlight = null; repaintOnEDT();
		}

		void pop(IntSupplier d) {
			if (stack.isEmpty()) { setStatus("Stack underflow: empty stack"); return; }
			Integer v = stack.peek(); setStatus("Pop " + v);
			highlight = v; repaintOnEDT(); stepDelay(d);
			stack.pop(); repaintOnEDT(); stepDelay(d);
			highlight = null; repaintOnEDT();
		}

		void peek(IntSupplier d) {
			if (stack.isEmpty()) { setStatus("Peek: stack is empty"); return; }
			Integer v = stack.peek(); setStatus("Peek top = " + v);
			highlight = v; repaintOnEDT(); stepDelay(d);
			highlight = null; repaintOnEDT();
		}
	}

	@FunctionalInterface interface IntSupplier { int getAsInt(); }
} 