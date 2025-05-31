#include <iostream>
#include <vector>
#include <algorithm>
#include <unordered_map>

using namespace std;

vector<int> tree;
vector<int> a;
vector<int> countLeft;
vector<int> countRight;
int n;
int initialN;

void update(int v, int l, int r, int i, int value) {
    if (l + 1 == r) {
        tree[v] += value;
        return;
    }
    int m = l + (r - l) / 2;
    if (i < m) {
        update(2 * v + 1, l, m, i, value);
    } else {
        update(2 * v + 2, m, r, i, value);
    }
    tree[v] = tree[2 * v + 1] + tree[2 * v + 2];
}

int get(int v, int l, int r, int ql, int qr) {
    if (qr <= l || ql >= r) {
        return 0;
    }
    if (ql <= l && r <= qr) {
        return tree[v];
    }
    int m = l + (r - l) / 2;
    int sum1 = get(2 * v + 1, l, m, ql, qr);
    int sum2 = get(2 * v + 2, m, r, ql, qr);
    return sum1 + sum2;
}

void build(int v, int l, int r) {
    if (l + 1 == r) {
        tree[v] = 0;
        return;
    }
    int m = l + (r - l) / 2;
    build(2 * v + 1, l, m);
    build(2 * v + 2, m, r);
    tree[v] = tree[2 * v + 1] + tree[2 * v + 2];
}

void coordComp() {
    vector<int> b = a;
    sort(b.begin(), b.end());
    unordered_map<int, int> map;
    for (int i = 0; i < initialN; i++) {
        map[b[i]] = i;
    }
    for (int i = 0; i < initialN; i++) {
        a[i] = map[a[i]];
    }
}



int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> initialN;
    a.resize(initialN);
    countLeft.resize(initialN);
    countRight.resize(initialN);
    for (int i = 0; i < initialN; i++) {
        cin >> a[i];
    }
    int b = 1;
    int pow = 0;
    while (true) {
        if (b >= initialN) {
            break;
        }
        b *= 2;
        pow++;
    }
    n = (1 << pow);
    tree.resize(2 * n - 1);
    coordComp();
    build(0, 0, n);
    update(0, 0, n, a[0], 1);
    for (int i = 1; i < initialN - 1; i++) {
        countLeft[i] = get(0, 0, n, a[i] + 1, initialN);
        update(0, 0, n, a[i], 1);
    }

    build(0, 0, n);
    update(0, 0, n, a[initialN - 1], 1);
    for (int i = initialN - 2; i > 0; i--) {
        countRight[i] = get(0, 0, n, 0, a[i]);
        update(0, 0, n, a[i], 1);
    }

    long long count = 0LL;
    for (int i = 1; i < initialN - 1; i++) {
        count += (long long) countLeft[i] * countRight[i];
    }
    cout << count << endl;

    return 0;
}