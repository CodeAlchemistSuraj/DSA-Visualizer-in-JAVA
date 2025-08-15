package pathfinder.algorithms;

import pathfinder.model.Grid;
import pathfinder.model.Node;

/**
 * D* Lite placeholder: for visualization we reuse A* behavior. Implementing
 * full incremental D* Lite is non-trivial; this provides a demo entry.
 */
public class DStarLite implements PathfindingAlgorithm {
    @Override
    public void findPath(Grid grid, Node start, Node end, Runnable onStep) {
        AStarSearch aStar = new AStarSearch();
        aStar.findPath(grid, start, end, onStep);
    }
}
