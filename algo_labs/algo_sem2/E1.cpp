#include <iostream>
#include <vector>
using namespace std;

struct Node {
    int maxInRange;

    Node(int maxInRange = INT_MIN) : maxInRange(maxInRange) {}
};

Node combine(const Node &leftChild, const Node &rightChild) {
    return Node(max(leftChild.maxInRange, rightChild.maxInRange));
}

void build(vector<Node> &tree, const vector<int> &a, int v, int l, int r) {
    if (l == r) {
        tree[v] = Node(a[l]);
    } else {
        int m = (l + r) / 2;
        build(tree, a, v * 2 + 1, l, m);
        build(tree, a, v * 2 + 2, m + 1, r);
        tree[v] = combine(tree[v * 2 + 1], tree[v * 2 + 2]);
    }
}

int get(const vector<Node> &tree, int v, int l, int r, int ql, int qr, int k) {
    if (qr <= l || ql >= r) {
        return -1;
    }
    if (ql <= l && r <= qr) {
        if (tree[v].maxInRange < k) {
            return -1;
        }
        while (r - l > 1) {
            int m = (l + r) / 2;
            if (tree[2 * v + 1].maxInRange >= k) {
                v = 2 * v + 1;
                r = m;
            } else {
                v = 2 * v + 2;
                l = m;
            }
        }
        return l + 1;
    }
    int m = (l + r) / 2;
    int res = get(tree, 2 * v + 1, l, m, ql, qr, k);
    if (res != -1) {
        return res;
    }
    return get(tree, 2 * v + 2, m, r, ql, qr, k);
}

void set(vector<Node> &tree, int v, int l, int r, int i, int val) {
    if (r - l == 1) {
        tree[v] = Node(val);
        return;
    }
    int m = (r + l) / 2;
    if (i < m) {
        set(tree, v * 2 + 1, l, m, i, val);
    } else {
        set(tree, v * 2 + 2, m, r, i, val);
    }
    tree[v] = combine(tree[v * 2 + 1], tree[v * 2 + 2]);
}

int main() {
    int n, m;
    cin >> n >> m;
    int pow = 1 << (32 - __builtin_clz(n - 1));
    vector<int> array(pow, INT_MIN);
    for (int i = 0; i < n; i++) {
        cin >> array[i];
    }
    vector<Node> tree(4 * pow);
    build(tree, array, 1, 0, pow - 1);
    string ans;
    while (m > 0) {
        int t, f, s;
        cin >> t >> f >> s;
        if (t == 0) {
            set(tree, 1, 0, pow, f - 1, s);
        } else if (t == 1) {
            ans += to_string(get(tree, 1, 0, pow, f - 1, pow, s)) + '\n';
        }
        m--;
    }
    cout << ans;
    return 0;
}