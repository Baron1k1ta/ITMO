#include <iostream>
#include <vector>
#include <string>
using namespace std;

vector<int> tree;
vector<int> add;
int N;

void build(const vector<int>& numbers, int v, int l, int r) {
    if (l == r) {
        tree[v] = numbers[l];
    } else {
        int m = (l + r) / 2;
        build(numbers, 2 * v, l, m);
        build(numbers, 2 * v + 1, m + 1, r);
        tree[v] = max(tree[2 * v], tree[2 * v + 1]);
    }
}

void push(int v) {
    if (add[v] != 0) {
        tree[2 * v] += add[v];
        tree[2 * v + 1] += add[v];
        add[2 * v] += add[v];
        add[2 * v + 1] += add[v];
        add[v] = 0;
    }
}

void addRange(int v, int l, int r, int tree_l, int tree_r, int x) {
    if (l > tree_r || r < tree_l) {
        return;
    }
    if (l <= tree_l && tree_r <= r) {
        tree[v] += x;
        add[v] += x;
    } else {
        push(v);
        int m = (tree_l + tree_r) / 2;
        addRange(2 * v, l, r, tree_l, m, x);
        addRange(2 * v + 1, l, r, m + 1, tree_r, x);
        tree[v] = max(tree[2 * v], tree[2 * v + 1]);
    }
}

int get(int v, int l, int r, int tree_l, int tree_r) {
    if (l > tree_r || r < tree_l) {
        return INT_MIN;
    }
    if (l <= tree_l && tree_r <= r) {
        return tree[v];
    } else {
        push(v);
        int m = (tree_l + tree_r) / 2;
        return max(get(2 * v, l, r, tree_l, m), get(2 * v + 1, l, r, m + 1, tree_r));
    }
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(0);
    cout.tie(0);

    cin >> N;
    vector<int> numbers(N);
    for (int i = 0; i < N; i++) {
        cin >> numbers[i];
    }

    int power = 1;
    while (power < N) {
        power *= 2;
    }

    tree.assign(power * 2, 0);
    add.assign(power * 2, 0);

    build(numbers, 1, 0, N - 1);

    int M;
    cin >> M;
    string output;
    for (int i = 0; i < M; i++) {
        string letter;
        cin >> letter;
        if (letter == "m") {
            int l, r;
            cin >> l >> r;
            output += to_string(get(1, l - 1, r - 1, 0, N - 1)) + " ";
        } else {
            int l, r, x;
            cin >> l >> r >> x;
            addRange(1, l - 1, r - 1, 0, N - 1, x);
        }
    }
    cout << output.substr(0, output.size() - 1) << "\n";
    return 0;
}