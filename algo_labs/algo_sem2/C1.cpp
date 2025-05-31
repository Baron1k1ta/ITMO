#include <iostream>
#include <vector>

using namespace std;

struct Node {
    long long pref;
    long long suf;
    long long ans;
    bool isZero;

    Node(long long pref, long long suf, long long ans, bool isZero)
        : pref(pref), suf(suf), ans(ans), isZero(isZero) {}
    Node() : pref(0), suf(0), ans(INT_MIN), isZero(false) {}
};

Node combine(Node left, Node right) {
    if (left.ans == INT_MIN) {
        return right;
    }
    if (right.ans == INT_MIN) {
        return left;
    }
    long long newAns = max(left.ans, right.ans);
    long long newPref = left.pref;
    long long newSuf = right.suf;
    if (left.isZero) {
        if (right.isZero) {
            return Node(left.pref + right.pref, left.pref + right.pref, left.pref + right.pref, true);
        } else {
            newPref = left.pref + right.pref;
        }
    } else if (right.isZero) {
        newSuf = left.suf + right.pref;
    }
    if (left.suf != 0 && right.pref != 0) {
        newAns = max(newAns, left.suf + right.pref);
    }
    return Node(newPref, newSuf, newAns, false);
}

void build(vector<Node> &tree, vector<long long> &a, int v, int l, int r) {
    if (l == r) {
        if (a[l] == 0) {
            tree[v] = Node(1, 1, 1, true);
        } else {
            tree[v] = Node(0, 0, 0, false);
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
        return Node(INT_MIN, INT_MIN, INT_MIN, false);
    }
    if (ql <= l && r <= qr) {
        return tree[v];
    }
    int m = (l + r) / 2;
    Node left = get(tree, v * 2 + 1, l, m, ql, qr);
    Node right = get(tree, v * 2 + 2, m, r, ql, qr);
    return combine(left, right);
}

void set(vector<Node> &tree, int v, int l, int r, int pos, int val) {
    if (r - l == 1) {
        if (val == 0) {
            tree[v] = Node(1, 1, 1, true);
        } else {
            tree[v] = Node(0, 0, 0, false);
        }
        return;
    }
    int m = (r + l) / 2;
    if (pos < m) {
        set(tree, v * 2 + 1, l, m, pos, val);
    } else {
        set(tree, v * 2 + 2, m, r, pos, val);
    }
    tree[v] = combine(tree[v * 2 + 1], tree[v * 2 + 2]);
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
        if (command == "QUERY") {
            int l, r;
            cin >> l >> r;
            cout << get(tree, 1, 0, pow, l - 1, r).ans << "\n";
        } else {
            int pos, val;
            cin >> pos >> val;
            set(tree, 1, 0, pow, pos - 1, val);
        }
    }

    return 0;
}