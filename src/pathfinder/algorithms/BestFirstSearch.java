package pathfinder.algorithms;

import java.util.Comparator;
import java.util.PriorityQueue;
import pathfinder.model.Grid;
import pathfinder.model.Node;

/**
 * Best-First Search: prioritize nodes by heuristic (h-cost) only.
 */
public class BestFirstSearch implements PathfindingAlgorithm {
    @Override
    public void findPath(Grid grid, Node start, Node end, Runnable onStep) {
        if (start == null || end == null) return;

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(Node::getFCost));

        for (int r = 0; r < grid.getRows(); r++) for (int c = 0; c < grid.getCols(); c++) {
            Node n = grid.getNode(r, c);
            n.setVisited(false);
            n.setFrontier(false);
            n.setInPath(false);
            n.setPrevious(null);
        }

        start.setFrontier(true);
        start.setFCost(heuristic(start, end));
        open.add(start);
        onStep.run();

        while (!open.isEmpty()) {
            Node current = open.poll();
            if (current.isVisited()) continue;
            current.setFrontier(false);
            current.setVisited(true);
            onStep.run();
            if (current.equals(end)) { reconstructPath(current); return; }
            for (Node nb : grid.getNeighbors(current)) {
                if (nb.isWall() || nb.isVisited()) continue;
                double h = heuristic(nb, end);
                nb.setFCost(h);
                nb.setPrevious(current);
                if (!nb.isFrontier()) { nb.setFrontier(true); open.add(nb); }
            }
        }
    }

    private double heuristic(Node a, Node b) {
        return Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getCol() - b.getCol());
    }
}
