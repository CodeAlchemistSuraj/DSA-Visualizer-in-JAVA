package pathfinder.algorithms;

import java.util.Comparator;
import java.util.PriorityQueue;
import pathfinder.model.Grid;
import pathfinder.model.Node;

public class DijkstraSearch implements PathfindingAlgorithm {
	@Override
	public void findPath(Grid grid, Node start, Node end, Runnable onStep) {
		if (start == null || end == null) return;

		PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(Node::getGCost));
		for (int r = 0; r < grid.getRows(); r++) {
			for (int c = 0; c < grid.getCols(); c++) {
				grid.getNode(r, c).setGCost(Double.POSITIVE_INFINITY);
			}
		}
		start.setGCost(0);
		pq.add(start);
		start.setFrontier(true);
		onStep.run();

		while (!pq.isEmpty()) {
			Node current = pq.poll();
			if (current.isVisited()) continue;
			current.setFrontier(false);
			current.setVisited(true);
			onStep.run();
			if (current.equals(end)) {
				reconstructPath(current);
				return;
			}
			for (Node neighbor : grid.getNeighbors(current)) {
				if (neighbor.isWall() || neighbor.isVisited()) continue;
				double tentative = current.getGCost() + 1.0; // uniform weight
				if (tentative < neighbor.getGCost()) {
					neighbor.setGCost(tentative);
					neighbor.setPrevious(current);
					neighbor.setFrontier(true);
					pq.add(neighbor);
				}
			}
		}
	}
} 