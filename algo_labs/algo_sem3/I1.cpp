#include <iostream>
#include <vector>
#include <queue>
#include <limits>

using namespace std;

struct Edge {
    int to;
    int weight;
};

struct Node {
    int minValue;
    int deg;
    vector<Edge> edges;
};

int main() {
    int n, m, s, t;
    cin >> n >> m >> s >> t;

    vector<Node> graph(n);
    s--;
    t--;


    for (int i = 0; i < m; i++) {
        int u, v, w;
        cin >> u >> v >> w;
        graph[u - 1].edges.push_back({v - 1, w});
        graph[v - 1].deg++;
    }


    queue<int> q;
    for (int i = 0; i < n; i++) {
        if (graph[i].deg == 0) {
            q.push(i);
        }
    }


    vector<int> topSort;
    while (!q.empty()) {
        int v = q.front();
        q.pop();
        topSort.push_back(v);
        for (const auto& e : graph[v].edges) {
            graph[e.to].deg--;
            if (graph[e.to].deg == 0) {
                q.push(e.to);
            }
        }
    }


    int start = -1, end = -1;
    for (int i = 0; i < topSort.size(); i++) {
        if (topSort[i] == s) {
            start = i;
        }
        if (topSort[i] == t) {
            end = i;
        }
    }


    for (auto& node : graph) {
        node.minValue = numeric_limits<int>::max();
    }


    graph[topSort[start]].minValue = 0;


    for (int i = start; i < end; i++) {
        if (graph[topSort[i]].minValue == numeric_limits<int>::max()) {
            continue;
        }
        for (const auto& e : graph[topSort[i]].edges) {
            graph[e.to].minValue = min(graph[e.to].minValue, graph[topSort[i]].minValue + e.weight);
        }
    }


    if (graph[topSort[end]].minValue == numeric_limits<int>::max()) {
        cout << "Unreachable" << endl;
        return 0;
    }

    cout << graph[topSort[end]].minValue << endl;
    return 0;
}