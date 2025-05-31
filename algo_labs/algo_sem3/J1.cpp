#include <iostream>
#include <vector>

using namespace std;

struct Node {
    int time;
    vector<int> edges;
};

vector<Node> graph;
vector<bool> mark;
vector<int> result;
long long T;

void dfs(int u) {
    mark[u] = true;
    for (int v : graph[u].edges) {
        if (!mark[v]) {
            dfs(v);
        }
    }
    T += graph[u].time;
    result.push_back(u);
}

int main() {
    int n, t, count;
    cin >> n;
    graph.resize(n);
    mark.resize(n, false);

    for (int i = 0; i < n; i++) {
        cin >> t;
        graph[i].time = t;
    }

    for (int i = 0; i < n; i++) {
        cin >> count;
        for (int j = 0; j < count; j++) {
            cin >> t;
            graph[i].edges.push_back(t - 1);
        }
    }

    dfs(0);
    cout << T << " " << result.size() << endl;
    for (int v : result) {
        cout << v + 1 << " ";
    }

    return 0;
}