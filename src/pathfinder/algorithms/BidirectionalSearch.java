package pathfinder.algorithms;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import pathfinder.model.Grid;
import pathfinder.model.Node;

/**
 * Bidirectional BFS from start and end; meets in the middle when searches intersect.
 */
public class BidirectionalSearch implements PathfindingAlgorithm {
    @Override
    public void findPath(Grid grid, Node start, Node end, Runnable onStep) {
        if (start == null || end == null) return;

        Queue<Node> qStart = new ArrayDeque<>();
        Queue<Node> qEnd = new ArrayDeque<>();
        Set<Node> seenStart = new HashSet<>();
        Set<Node> seenEnd = new HashSet<>();

        qStart.add(start); start.setFrontier(true); seenStart.add(start);
        qEnd.add(end); end.setFrontier(true); seenEnd.add(end);
        onStep.run();

        while (!qStart.isEmpty() && !qEnd.isEmpty()) {
            if (advanceFrontier(grid, qStart, seenStart, seenEnd, onStep)) return;
            if (advanceFrontier(grid, qEnd, seenEnd, seenStart, onStep)) return;
        }
    }

    private boolean advanceFrontier(Grid grid, Queue<Node> q, Set<Node> own, Set<Node> other, Runnable onStep) {
        if (q.isEmpty()) return false;
        Node cur = q.poll();
        cur.setFrontier(false);
        cur.setVisited(true);
        onStep.run();
        for (Node nb : grid.getNeighbors(cur)) {
            if (nb.isWall() || own.contains(nb) || nb.isVisited()) continue;
            nb.setPrevious(cur);
            nb.setFrontier(true);
            q.add(nb);
            own.add(nb);
            if (other.contains(nb)) {
                // Found meeting point; reconstruct by following previous pointers from both sides
                reconstructMeeting(nb, other);
                return true;
            }
        }
        return false;
    }

    private void reconstructMeeting(Node meeting, Set<Node> otherSeen) {
        // Mark path back to starts for visualization
        Node cur = meeting;
        while (cur != null) { cur.setInPath(true); cur = cur.getPrevious(); }
        // other side: nodes in otherSeen may have previous pointers; mark them as inPath where applicable
        for (Node n : otherSeen) {
            Node p = n;
            while (p != null) { p.setInPath(true); p = p.getPrevious(); }
        }
    }
}
