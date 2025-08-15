package pathfinder.algorithms;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import pathfinder.model.Grid;
import pathfinder.model.Node;

public class AStarSearch implements PathfindingAlgorithm {
	@Override
	public void findPath(Grid grid, Node start, Node end, Runnable onStep) {
		if (start == null || end == null) return;

		PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(Node::getFCost));
		Set<Node> openSet = new HashSet<>();
		for (int r = 0; r < grid.getRows(); r++) {
			for (int c = 0; c < grid.getCols(); c++) {
				Node n = grid.getNode(r, c);
				n.setGCost(Double.POSITIVE_INFINITY);
				n.setFCost(Double.POSITIVE_INFINITY);
			}
		}

		start.setGCost(0);
		start.setFCost(heuristic(start, end));
		open.add(start);
		openSet.add(start);
		start.setFrontier(true);
		onStep.run();

		while (!open.isEmpty()) {
			Node current = open.poll();
			openSet.remove(current);
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
				double tentativeG = current.getGCost() + 1.0;
				if (tentativeG < neighbor.getGCost()) {
					neighbor.setPrevious(current);
					neighbor.setGCost(tentativeG);
					neighbor.setFCost(tentativeG + heuristic(neighbor, end));
					if (!openSet.contains(neighbor)) {
						open.add(neighbor);
						openSet.add(neighbor);
						neighbor.setFrontier(true);
					}
				}
			}
		}
	}

	private double heuristic(Node a, Node b) {
		return Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getCol() - b.getCol());
	}
} 