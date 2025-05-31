#include <iostream>
#include <vector>

using namespace std;

struct Node {
    int deg = 0;
    vector<int> edges;
};

int main() {
    int n, m;
    cin >> n >> m;
    vector<Node> graph(n);

    int u, v;
    for (int i = 0; i < m; i++) {
        cin >> u >> v;
        graph[u - 1].edges.push_back(v - 1);
        graph[v - 1].deg++;
    }

    for (int i = 0; i < n; i++) {
        cin >> u;
        u--;
        if (graph[u].deg != 0) {
            cout << "NO" << endl;
            return 0;
        }
        for (int e : graph[u].edges) {
            graph[e].deg--;
        }
    }
    cout << "YES" << endl;
    return 0;
}