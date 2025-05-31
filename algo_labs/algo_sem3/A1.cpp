#include <iostream>
#include <vector>

using namespace std;

struct Node {
    int n;
    vector<int> edges;
};

vector<Node> graph;
vector<bool> mark;

void dfs(int u) {
    mark[u] = true;
    for (int v : graph[u].edges) {
        if (!mark[v]) {
            cout << u + 1 << " " << v + 1 << endl;
            dfs(v);
        }
    }
}

int main() {
    int n, m;
    cin >> n >> m;

    graph.resize(n);
    mark.resize(n, false);

    for (int i = 0; i < n; ++i) {
        graph[i].n = i;
    }

    for (int i = 0; i < m; ++i) {
        int u, v;
        cin >> u >> v;
        graph[u - 1].edges.push_back(v - 1);
        graph[v - 1].edges.push_back(u - 1);
    }

    dfs(0);
    return 0;
}