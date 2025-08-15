package pathfinder.algorithms;

import pathfinder.model.Grid;
import pathfinder.model.Node;

/**
 * Floyd-Warshall computes all-pairs shortest paths. For grid visualization we will
 * run a simplified version that computes distances between all non-wall nodes and
 * then highlight a shortest path between a chosen pair (start,end) by following
 * predecessor info.
 * Note: This is a heavier algorithm (O(n^3)) and with visualization it will be slow
 * on large grids; intended mainly for small grids/demos.
 */
public class FloydWarshall implements PathfindingAlgorithm {
    @Override
    public void findPath(Grid grid, Node start, Node end, Runnable onStep) {
        if (start == null || end == null) return;

        int rows = grid.getRows();
        int cols = grid.getCols();
        int n = rows * cols;
        Node[] nodes = new Node[n];
        for (int r = 0, idx = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++, idx++) nodes[idx] = grid.getNode(r, c);
        }

        final double INF = Double.POSITIVE_INFINITY;
        double[][] dist = new double[n][n];
        int[][] next = new int[n][n];

        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) { dist[i][j] = (i==j)?0:INF; next[i][j] = -1; }

        for (int i = 0; i < n; i++) {
            Node a = nodes[i];
            if (a.isWall()) continue;
            for (Node b : grid.getNeighbors(a)) {
                int j = b.getRow() * cols + b.getCol();
                dist[i][j] = 1.0;
                next[i][j] = j;
            }
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }
            onStep.run();
        }

        // reconstruct path from start to end using next
    int si = start.getRow() * cols + start.getCol();
    int ei = end.getRow() * cols + end.getCol();
        if (next[si][ei] == -1) return;
        int cur = si;
        while (cur != ei && cur != -1) {
            nodes[cur].setInPath(true);
            cur = next[cur][ei];
            onStep.run();
        }
        nodes[ei].setInPath(true);
    }
}
