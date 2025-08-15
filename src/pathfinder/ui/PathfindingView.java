package pathfinder.ui;

import java.awt.BorderLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import pathfinder.algorithms.*;
import pathfinder.model.Grid;

public class PathfindingView extends JPanel {
	private final GridPanel gridPanel;
	private final ControlPanel controlPanel;
	private SwingWorker<Void, Void> worker;

	public PathfindingView() {
		super(new BorderLayout());
		gridPanel = new GridPanel(25, 25, 24);
		controlPanel = new ControlPanel();
		add(controlPanel, BorderLayout.NORTH);
		add(gridPanel, BorderLayout.CENTER);
		wireEvents();
	}

	private void wireEvents() {
		controlPanel.onStart(() -> {
			if (worker != null && !worker.isDone()) return;
			String algorithmName = controlPanel.getSelectedAlgorithm();
			PathfindingAlgorithm algorithm = createAlgorithm(algorithmName);
			if (algorithm == null) {
				JOptionPane.showMessageDialog(this, "Please select an algorithm.");
				return;
			}
			if (!gridPanel.hasStartEnd()) {
				JOptionPane.showMessageDialog(this, "Please set both Start and End nodes (Shift+Click for Start, Ctrl+Click for End).");
				return;
			}
			controlPanel.setControlsEnabled(false);
			Grid grid = gridPanel.getGrid();
			grid.resetSearchState();
			gridPanel.repaint();
			worker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					algorithm.findPath(grid, grid.getStartNode(), grid.getEndNode(), () -> {
						try { Thread.sleep(controlPanel.getSpeedDelayMs()); } catch (InterruptedException ignored) {}
						gridPanel.repaintOnEDT();
					});
					return null;
				}
				@Override
				protected void done() {
					gridPanel.repaintOnEDT();
					controlPanel.setControlsEnabled(true);
				}
			};
			worker.execute();
		});
		controlPanel.onClear(() -> {
			if (worker != null && !worker.isDone()) worker.cancel(true);
			gridPanel.getGrid().clearWallsAndSearch();
			gridPanel.repaintOnEDT();
		});
		controlPanel.onResize(() -> {
			if (worker != null && !worker.isDone()) worker.cancel(true);
			int rows = controlPanel.getRows();
			int cols = controlPanel.getCols();
			gridPanel.resizeGrid(rows, cols);
			revalidate();
		});
		gridPanel.setToolSupplier(controlPanel::getActiveTool);
	}

	private PathfindingAlgorithm createAlgorithm(String name) {
		if (name == null) return null;
		switch (name) {
			case "Breadth-First Search (BFS)": return new BreadthFirstSearch();
			case "Dijkstra's Algorithm": return new DijkstraSearch();
			case "A* Search": return new AStarSearch();
			default: return null;
		}
	}
} 