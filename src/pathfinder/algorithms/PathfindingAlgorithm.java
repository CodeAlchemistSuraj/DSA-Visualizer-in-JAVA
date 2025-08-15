package pathfinder.algorithms;

import java.util.function.Consumer;
import pathfinder.model.Grid;
import pathfinder.model.Node;

public interface PathfindingAlgorithm {
	void findPath(Grid grid, Node start, Node end, Runnable onStep);

	default void reconstructPath(Node end) {
		Node current = end;
		while (current != null) {
			current.setInPath(true);
			current = current.getPrevious();
		}
	}
} 