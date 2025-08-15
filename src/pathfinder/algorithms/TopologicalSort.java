package pathfinder.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Topological sort for DAGs using DFS
 */
public class TopologicalSort {
    public static List<Integer> sort(int n, List<Integer>[] adj) {
        boolean[] vis = new boolean[n];
        boolean[] onStack = new boolean[n];
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < n; i++) if (!vis[i]) {
            if (!dfs(i, adj, vis, onStack, stack)) return null; // cycle detected
        }
        List<Integer> res = new ArrayList<>();
        while (!stack.isEmpty()) res.add(stack.pop());
        return res;
    }

    private static boolean dfs(int u, List<Integer>[] adj, boolean[] vis, boolean[] onStack, Stack<Integer> stack) {
        vis[u] = true; onStack[u] = true;
        for (int v : adj[u]) {
            if (!vis[v]) {
                if (!dfs(v, adj, vis, onStack, stack)) return false;
            } else if (onStack[v]) return false; // cycle
        }
        onStack[u] = false; stack.push(u);
        return true;
    }
}
