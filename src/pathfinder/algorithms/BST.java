package pathfinder.algorithms;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple integer BST with common operations useful for visualization.
 */
public class BST {
    private static class Node { int val; Node left, right; Node(int v){val=v;} }
    private Node root;

    public void insert(int v) { root = insertRec(root, v); }
    private Node insertRec(Node n, int v) {
        if (n == null) return new Node(v);
        if (v < n.val) n.left = insertRec(n.left, v);
        else if (v > n.val) n.right = insertRec(n.right, v);
        return n;
    }

    public boolean contains(int v) { return containsRec(root, v); }
    private boolean containsRec(Node n, int v) {
        if (n == null) return false;
        if (v == n.val) return true;
        return v < n.val ? containsRec(n.left, v) : containsRec(n.right, v);
    }

    public void delete(int v) { root = deleteRec(root, v); }
    private Node deleteRec(Node n, int v) {
        if (n == null) return null;
        if (v < n.val) n.left = deleteRec(n.left, v);
        else if (v > n.val) n.right = deleteRec(n.right, v);
        else {
            if (n.left == null) return n.right;
            if (n.right == null) return n.left;
            Node succ = minNode(n.right);
            n.val = succ.val;
            n.right = deleteRec(n.right, succ.val);
        }
        return n;
    }

    private Node minNode(Node n) { while (n.left != null) n = n.left; return n; }

    public List<Integer> inorder() { List<Integer> out = new ArrayList<>(); inorderRec(root, out); return out; }
    private void inorderRec(Node n, List<Integer> out) { if (n==null) return; inorderRec(n.left,out); out.add(n.val); inorderRec(n.right,out); }

    public List<Integer> preorder() { List<Integer> out = new ArrayList<>(); preorderRec(root, out); return out; }
    private void preorderRec(Node n, List<Integer> out) { if (n==null) return; out.add(n.val); preorderRec(n.left,out); preorderRec(n.right,out); }

    public List<Integer> postorder() { List<Integer> out = new ArrayList<>(); postorderRec(root, out); return out; }
    private void postorderRec(Node n, List<Integer> out) { if (n==null) return; postorderRec(n.left,out); postorderRec(n.right,out); out.add(n.val); }
}
