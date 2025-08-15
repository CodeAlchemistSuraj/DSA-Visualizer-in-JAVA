package pathfinder.algorithms;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import pathfinder.model.Grid;
import pathfinder.model.Node;

public class BreadthFirstSearch implements PathfindingAlgorithm {
	@Override
	public void findPath(Grid grid, Node start, Node end, Runnable onStep) {
		if (start == null || end == null) return;

		Queue<Node> queue = new ArrayDeque<>();
		Set<Node> visited = new HashSet<>();
		start.setGCost(0);
		queue.add(start);
		start.setFrontier(true);
		onStep.run();

		while (!queue.isEmpty()) {
			Node current = queue.poll();
			current.setFrontier(false);
			current.setVisited(true);
			onStep.run();
			if (current.equals(end)) {
				reconstructPath(current);
				return;
			}
			for (Node neighbor : grid.getNeighbors(current)) {
				if (neighbor.isWall() || visited.contains(neighbor) || neighbor.isVisited() || neighbor.isFrontier()) continue;
				if (!visited.contains(neighbor)) {
					neighbor.setPrevious(current);
					neighbor.setFrontier(true);
					queue.add(neighbor);
				}
			}
			visited.add(current);
		}
	}
} 