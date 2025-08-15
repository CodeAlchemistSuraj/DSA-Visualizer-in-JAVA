package pathfinder.algorithms;

import pathfinder.model.Grid;
import pathfinder.model.Node;

/**
 * Interface used by controllers to invoke pathfinding algorithms (DIP-friendly).
 */
public interface IPathfindingAlgorithm {
    void findPath(Grid grid, Node start, Node end, Runnable onStep);
}
