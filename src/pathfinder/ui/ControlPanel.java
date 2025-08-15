package pathfinder.ui;

import java.awt.FlowLayout;
import java.util.function.Supplier;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class ControlPanel extends JPanel {
	private final JButton startButton = new JButton("Start Visualization");
	private final JButton clearButton = new JButton("Clear Grid");
	private final JButton resizeButton = new JButton("Resize Grid");
	private final JComboBox<String> algorithmCombo = new JComboBox<>(new String[] {
		"Breadth-First Search (BFS)",
		"Dijkstra's Algorithm",
		"A* Search"
	});
	private final JSpinner rowsSpinner = new JSpinner(new SpinnerNumberModel(25, 5, 100, 1));
	private final JSpinner colsSpinner = new JSpinner(new SpinnerNumberModel(25, 5, 100, 1));
	private final JSlider speedSlider = new JSlider(0, 200, 15);
	private final JComboBox<GridPanel.Tool> toolCombo = new JComboBox<>(GridPanel.Tool.values());

	public ControlPanel() {
		super(new FlowLayout(FlowLayout.LEFT));
		add(new JLabel("Algorithm:"));
		add(algorithmCombo);
		add(startButton);
		add(clearButton);
		add(new JLabel("Rows:"));
		add(rowsSpinner);
		add(new JLabel("Cols:"));
		add(colsSpinner);
		add(resizeButton);
		add(new JLabel("Speed:"));
		speedSlider.setInverted(true);
		speedSlider.setToolTipText("Higher is faster");
		add(speedSlider);
		add(new JLabel("Tool:"));
		add(toolCombo);
	}

	public void onStart(Runnable action) { startButton.addActionListener(e -> action.run()); }
	public void onClear(Runnable action) { clearButton.addActionListener(e -> action.run()); }
	public void onResize(Runnable action) { resizeButton.addActionListener(e -> action.run()); }

	public String getSelectedAlgorithm() { return (String) algorithmCombo.getSelectedItem(); }
	public int getRows() { return (Integer) rowsSpinner.getValue(); }
	public int getCols() { return (Integer) colsSpinner.getValue(); }
	public GridPanel.Tool getActiveTool() { return (GridPanel.Tool) toolCombo.getSelectedItem(); }

	public int getSpeedDelayMs() {
		int value = speedSlider.getValue();
		int max = speedSlider.getMaximum();
		return Math.max(1, (max - value + 1));
	}

	public void setControlsEnabled(boolean enabled) {
		startButton.setEnabled(enabled);
		clearButton.setEnabled(enabled);
		resizeButton.setEnabled(enabled);
		algorithmCombo.setEnabled(enabled);
		rowsSpinner.setEnabled(enabled);
		colsSpinner.setEnabled(enabled);
		toolCombo.setEnabled(enabled);
	}
} 