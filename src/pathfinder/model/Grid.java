package pathfinder.model;

import java.util.ArrayList;
import java.util.List;

public class Grid {
	private final Node[][] nodes;
	private Node startNode;
	private Node endNode;

	public Grid(int rows, int cols) {
		nodes = new Node[rows][cols];
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				nodes[r][c] = new Node(r, c);
			}
		}
	}

	public int getRows() { return nodes.length; }
	public int getCols() { return nodes[0].length; }

	public Node getNode(int row, int col) { return nodes[row][col]; }

	public Node getStartNode() { return startNode; }
	public Node getEndNode() { return endNode; }

	public void setStart(int row, int col) {
		if (row < 0 || col < 0) {
			if (startNode != null) startNode.setStart(false);
			startNode = null;
			return;
		}
		Node node = nodes[row][col];
		if (node.isEnd()) setEnd(-1, -1);
		if (startNode != null) startNode.setStart(false);
		node.setWall(false);
		node.setStart(true);
		startNode = node;
	}

	public void setEnd(int row, int col) {
		if (row < 0 || col < 0) {
			if (endNode != null) endNode.setEnd(false);
			endNode = null;
			return;
		}
		Node node = nodes[row][col];
		if (node.isStart()) setStart(-1, -1);
		if (endNode != null) endNode.setEnd(false);
		node.setWall(false);
		node.setEnd(true);
		endNode = node;
	}

	public void resetSearchState() {
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getCols(); c++) {
				nodes[r][c].resetSearchState();
			}
		}
	}

	public void clearWallsAndSearch() {
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getCols(); c++) {
				Node n = nodes[r][c];
				if (!n.isStart() && !n.isEnd()) n.setWall(false);
				n.resetSearchState();
			}
		}
	}

	public List<Node> getNeighbors(Node node) {
		int r = node.getRow();
		int c = node.getCol();
		List<Node> result = new ArrayList<>(4);
		if (r > 0) result.add(nodes[r - 1][c]);
		if (r < getRows() - 1) result.add(nodes[r + 1][c]);
		if (c > 0) result.add(nodes[r][c - 1]);
		if (c < getCols() - 1) result.add(nodes[r][c + 1]);
		return result;
	}
} 