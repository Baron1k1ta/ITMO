package me.index.map;

import me.index.Holder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BTreeAdapt implements Storage {
    private Node root;
    private int treeSize = 0;

    public BTreeAdapt() {
    }

    @Override
    public void init(List<Long> keys, List<Object> values, int maxErr) {
        root = null;
        treeSize = keys.size();
        if (treeSize == 0) return;
        int n = keys.size();
        ArrayList<Integer> idx = new ArrayList<>(n);
        for (int i = 0; i < n; i++) idx.add(i);
        idx.sort(Comparator.comparingLong(keys::get));
        root = buildBalanced(keys, values, idx, 0, n - 1);
    }

    private Node buildBalanced(List<Long> keys, List<Object> values, List<Integer> idx, int lo, int hi) {
        if (lo > hi) return null;
        int mid = (lo + hi) >>> 1;
        int i = idx.get(mid);
        Node n = new Node(keys.get(i), values.get(i), null, null, null);
        n.left = buildBalanced(keys, values, idx, lo, mid - 1);
        n.right = buildBalanced(keys, values, idx, mid + 1, hi);
        if (n.left != null) n.left.parent = n;
        if (n.right != null) n.right.parent = n;
        return n;
    }

    @Override
    public int find(long key, Holder<Object> result) {
        if (root == null) return FAIL;
        Node n = findNode(key);
        splay(n);
        root = n;
        if (n.key == key) {
            result.v = n.value;
            return OK;
        }
        return FAIL;
    }

    @Override
    public int insert(long key, Object value) {
        if (root == null) {
            root = new Node(key, value, null, null, null);
            treeSize = 1;
            return OK;
        }
        Node n = findNode(key);
        splay(n);
        root = n;
        if (n.key == key) return FAIL;
        Node x = new Node(key, value, null, null, null);
        if (key < n.key) {
            x.left = n.left;
            x.right = n;
            if (n.left != null) n.left.parent = x;
            n.left = null;
            n.parent = x;
        } else {
            x.left = n;
            x.right = n.right;
            n.parent = x;
            if (n.right != null) n.right.parent = x;
            n.right = null;
        }
        root = x;
        treeSize++;
        return OK;
    }

    @Override
    public int remove(long key) {
        if (root == null) return FAIL;
        Node n = findNode(key);
        splay(n);
        root = n;
        if (n.key != key) return FAIL;
        if (n.left == null) {
            root = n.right;
            if (root != null) root.parent = null;
        } else if (n.right == null) {
            root = n.left;
            root.parent = null;
        } else {
            Node maxLeft = max(n.left);
            unlink(maxLeft);
            maxLeft.left = n.left;
            maxLeft.right = n.right;
            if (maxLeft.left != null) maxLeft.left.parent = maxLeft;
            if (maxLeft.right != null) maxLeft.right.parent = maxLeft;
            root = maxLeft;
            root.parent = null;
        }
        treeSize--;
        return OK;
    }

    private void unlink(Node x) {
        Node p = x.parent;
        Node child = x.left != null ? x.left : x.right;
        if (p.left == x) p.left = child;
        else p.right = child;
        if (child != null) child.parent = p;
        x.parent = null;
        x.left = null;
        x.right = null;
    }

    @Override
    public void resort(List<Long> keys, List<Object> vals) {
        inorder(root, keys, vals);
    }

    @Override
    public int size() {
        return treeSize;
    }

    private Node findNode(long key) {
        Node n = root;
        for (;;) {
            if (key == n.key) return n;
            if (key < n.key) {
                if (n.left == null) return n;
                n = n.left;
            } else {
                if (n.right == null) return n;
                n = n.right;
            }
        }
    }

    private Node max(Node n) {
        while (n.right != null) n = n.right;
        return n;
    }

    private void splay(Node x) {
        while (x.parent != null) {
            Node p = x.parent;
            Node g = p.parent;
            if (g == null) {
                rotate(x);
            } else if ((x == p.left && p == g.left) || (x == p.right && p == g.right)) {
                rotate(p);
                rotate(x);
            } else {
                rotate(x);
                rotate(x);
            }
        }
    }

    private void rotate(Node x) {
        Node p = x.parent;
        if (p == null) return;
        Node g = p.parent;
        if (x == p.left) {
            p.left = x.right;
            if (x.right != null) x.right.parent = p;
            x.right = p;
        } else {
            p.right = x.left;
            if (x.left != null) x.left.parent = p;
            x.left = p;
        }
        p.parent = x;
        x.parent = g;
        if (g != null) {
            if (g.left == p) g.left = x;
            else g.right = x;
        }
    }

    private static void inorder(Node n, List<Long> keys, List<Object> vals) {
        if (n == null) return;
        inorder(n.left, keys, vals);
        keys.add(n.key);
        vals.add(n.value);
        inorder(n.right, keys, vals);
    }

    private static class Node {
        long key;
        Object value;
        Node left, right, parent;

        Node(long key, Object value, Node left, Node right, Node parent) {
            this.key = key;
            this.value = value;
            this.left = left;
            this.right = right;
            this.parent = parent;
        }
    }
}
