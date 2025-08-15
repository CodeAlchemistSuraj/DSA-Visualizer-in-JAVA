package pathfinder.algorithms;

import java.util.ArrayList;
import java.util.List;

public class GraphUtils {
    @SuppressWarnings("unchecked")
    public static List<Integer>[] createAdj(int n) {
        List<Integer>[] adj = new List[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
        return adj;
    }
}
