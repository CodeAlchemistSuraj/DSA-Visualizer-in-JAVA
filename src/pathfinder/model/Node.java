package pathfinder.model;

import java.util.Objects;

public class Node {
	private final int row;
	private final int col;
	private boolean wall;
	private boolean start;
	private boolean end;
	private boolean frontier;
	private boolean visited;
	private boolean inPath;

	// Algorithm fields
	private double gCost; // distance from start
	private double fCost; // g + heuristic for A*
	private Node previous;

	public Node(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public int getRow() { return row; }
	public int getCol() { return col; }

	public boolean isWall() { return wall; }
	public void setWall(boolean wall) { this.wall = wall; }

	public boolean isStart() { return start; }
	public void setStart(boolean start) { this.start = start; }

	public boolean isEnd() { return end; }
	public void setEnd(boolean end) { this.end = end; }

	public boolean isFrontier() { return frontier; }
	public void setFrontier(boolean frontier) { this.frontier = frontier; }

	public boolean isVisited() { return visited; }
	public void setVisited(boolean visited) { this.visited = visited; }

	public boolean isInPath() { return inPath; }
	public void setInPath(boolean inPath) { this.inPath = inPath; }

	public double getGCost() { return gCost; }
	public void setGCost(double gCost) { this.gCost = gCost; }

	public double getFCost() { return fCost; }
	public void setFCost(double fCost) { this.fCost = fCost; }

	public Node getPrevious() { return previous; }
	public void setPrevious(Node previous) { this.previous = previous; }

	public void resetSearchState() {
		frontier = false;
		visited = false;
		inPath = false;
		previous = null;
		gCost = Double.POSITIVE_INFINITY;
		fCost = Double.POSITIVE_INFINITY;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Node)) return false;
		Node node = (Node) o;
		return row == node.row && col == node.col;
	}

	@Override
	public int hashCode() { return Objects.hash(row, col); }
} 