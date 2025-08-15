package pathfinder.algorithms;

import pathfinder.model.Grid;
import pathfinder.model.Node;

/**
 * Backwards-compatible adapter interface. Existing algorithm classes implement
 * this. New code should prefer `IPathfindingAlgorithm` for DI.
 */
public interface PathfindingAlgorithm extends IPathfindingAlgorithm {
	@Override
	void findPath(Grid grid, Node start, Node end, Runnable onStep);

	default void reconstructPath(Node end) {
		Node current = end;
		while (current != null) {
			current.setInPath(true);
			current = current.getPrevious();
		}
	}
}