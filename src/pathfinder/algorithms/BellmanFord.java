package pathfinder.algorithms;

import java.util.List;
import java.util.ArrayList;

/**
 * Bellman-Ford for graphs with possible negative weights. Returns distances array or null if negative cycle.
 */
public class BellmanFord {
    public static class Edge { public final int u, v; public final double w; public Edge(int u,int v,double w){this.u=u;this.v=v;this.w=w;} }

    public static double[] shortestPaths(int n, List<Edge> edges, int src) {
        double INF = Double.POSITIVE_INFINITY;
        double[] dist = new double[n];
        for (int i = 0; i < n; i++) dist[i] = INF;
        dist[src] = 0;
        for (int k = 0; k < n - 1; k++) {
            boolean changed = false;
            for (Edge e : edges) {
                if (dist[e.u] == INF) continue;
                if (dist[e.u] + e.w < dist[e.v]) { dist[e.v] = dist[e.u] + e.w; changed = true; }
            }
            if (!changed) break;
        }
        // check negative cycle
        for (Edge e : edges) if (dist[e.u] != INF && dist[e.u] + e.w < dist[e.v]) return null;
        return dist;
    }
}
