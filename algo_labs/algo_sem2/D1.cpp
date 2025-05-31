#include <iostream>
#include <vector>

using namespace std;

struct Node {
    long long left;
    long long right;
    int total;

    Node(long long left, long long right, int total)
        : left(left), right(right), total(total) {}
    Node() : left(0), right(0), total(0) {}
};

Node combine(Node left, Node right) {
    return Node(left.total, right.total, left.total + right.total);
}

void build(vector<Node> &tree, vector<long long> &a, int v, int l, int r) {
    if (l == r) {
        if (a[l] == 0) {
            tree[v] = Node(1, 0, 1);
        } else {
            tree[v] = Node(0, 0, 0);
        }
    } else {
        int m = (l + r) / 2;
        build(tree, a, v * 2 + 1, l, m);
        build(tree, a, v * 2 + 2, m + 1, r);
        tree[v] = combine(tree[v * 2 + 1], tree[v * 2 + 2]);
    }
}

Node get(vector<Node> &tree, int v, int l, int r, int ql, int qr) {
    if (qr <= l || ql >= r) {
        return Node(0, 0, 0);
    }
    if (ql <= l && r <= qr) {
        return tree[v];
    }
    int m = (l + r) / 2;
    Node left = get(tree, v * 2 + 1, l, m, ql, qr);
    Node right = get(tree, v * 2 + 2, m, r, ql, qr);
    return combine(left, right);
}

int query(vector<Node> &tree, int v, int l, int r, int k) {
    if (r - l == 1) {
        return l + 1;
    }
    int m = (l + r) / 2;
    if (tree[2 * v + 1].total >= k) {
        return query(tree, 2 * v + 1, l, m, k);
    } else {
        return query(tree, 2 * v + 2, m, r, k - tree[2 * v + 1].total);
    }
}

void set(vector<Node> &tree, int v, int l, int r, int pos, int val) {
    if (r - l == 1) {
        if (val == 0) {
            tree[v] = Node(1, 0, 1);
        } else {
            tree[v] = Node(0, 0, 0);
        }
        return;
    }
    int m = (r + l) / 2;
    if (pos < m) {
        set(tree, 2 * v + 1, l, m, pos, val);
    } else {
        set(tree, 2 * v + 2, m, r, pos, val);
    }
    tree[v] = combine(tree[2 * v + 1], tree[2 * v + 2]);
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;
    int pow = 1 << (32 - __builtin_clz(n - 1));
    vector<long long> array(pow, 0);
    for (int i = 0; i < n; i++) {
        cin >> array[i];
    }
    int m;
    cin >> m;
    vector<Node> tree(4 * pow);
    build(tree, array, 1, 0, pow - 1);

    string command;
    while (m--) {
        cin >> command;
        if (command == "s") {
            int l, r, k;
            cin >> l >> r >> k;
            int curr = get(tree, 1, 0, pow, l - 1, r).total;
            if (curr < k) {
                cout << "-1 ";
            } else {
                cout << query(tree, 1, 0, pow, k + get(tree, 1, 0, pow, 0, r).total - curr) << " ";
            }
        } else {
            int pos, val;
            cin >> pos >> val;
            set(tree, 1, 0, pow, pos - 1, val);
        }
    }

    return 0;
}