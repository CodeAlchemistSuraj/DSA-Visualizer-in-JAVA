package pathfinder.ui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import pathfinder.algorithms.IPathfindingAlgorithm;
import pathfinder.algorithms.BreadthFirstSearch;
import pathfinder.algorithms.DijkstraSearch;
import pathfinder.algorithms.AStarSearch;
import pathfinder.controller.PathfindingController;

public class PathfindingView extends JPanel {
	private final GridPanel gridPanel;
	private final ControlPanel controlPanel;
	private final PathfindingController controller;

	public PathfindingView() {
		super(new BorderLayout());
		gridPanel = new GridPanel(25, 25, 24);
		controlPanel = new ControlPanel();
		add(controlPanel, BorderLayout.NORTH);
		add(gridPanel, BorderLayout.CENTER);
		// Provide a factory so the controller can create algorithm based on current selection
		controller = new pathfinder.controller.PathfindingController(
			gridPanel,
			controlPanel,
			() -> createAlgorithm(controlPanel.getSelectedAlgorithm())
		);
	}

	private IPathfindingAlgorithm createAlgorithm(String name) {
		if (name == null) return null;
		switch (name) {
			case "Breadth-First Search (BFS)": return new BreadthFirstSearch();
			case "Dijkstra's Algorithm": return new DijkstraSearch();
			case "A* Search": return new AStarSearch();
			case "Best-First Search": return new pathfinder.algorithms.BestFirstSearch();
			case "Bidirectional Search": return new pathfinder.algorithms.BidirectionalSearch();
			case "Floyd-Warshall": return new pathfinder.algorithms.FloydWarshall();
			case "D* Lite (demo)": return new pathfinder.algorithms.DStarLite();
			default: return null;
		}
	}
}