package pathfinder.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Simple DFS for adjacency-list graphs (nodes indexed 0..n-1). Provides traversal and component discovery.
 */
public class DFS {
    public static void dfs(int n, List<Integer>[] adj, int start, Consumer<Integer> visit) {
        boolean[] vis = new boolean[n];
        dfsRec(start, adj, vis, visit);
    }

    private static void dfsRec(int u, List<Integer>[] adj, boolean[] vis, Consumer<Integer> visit) {
        if (vis[u]) return;
        vis[u] = true;
        if (visit != null) visit.accept(u);
        for (int v : adj[u]) dfsRec(v, adj, vis, visit);
    }

    public static List<List<Integer>> connectedComponents(int n, List<Integer>[] adj) {
        boolean[] vis = new boolean[n];
        List<List<Integer>> comps = new ArrayList<>();
        for (int i = 0; i < n; i++) if (!vis[i]) {
            List<Integer> comp = new ArrayList<>();
            collect(i, adj, vis, comp);
            comps.add(comp);
        }
        return comps;
    }

    private static void collect(int u, List<Integer>[] adj, boolean[] vis, List<Integer> out) {
        if (vis[u]) return;
        vis[u] = true; out.add(u);
        for (int v : adj[u]) collect(v, adj, vis, out);
    }
}
