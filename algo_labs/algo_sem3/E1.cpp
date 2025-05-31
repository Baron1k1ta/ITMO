#include <iostream>
#include <vector>
#include <cstdlib>
using namespace std;

struct Node {
    int color;
    vector<int> edges;
};

vector<Node> graph;
vector<bool> visited;

void colorGraph(int nodeIndex, int currentColor) {
    visited[nodeIndex] = true;
    graph[nodeIndex].color = currentColor;

    for (int neighbor : graph[nodeIndex].edges) {
        if (!visited[neighbor]) {
            colorGraph(neighbor, 3 - currentColor);
        } else if (graph[neighbor].color == currentColor) {
            cout << -1 << endl;
            exit(0);
        }
    }
}

void initializeGraph(int nodeCount) {
    graph.resize(nodeCount);
    visited.resize(nodeCount, false);
}

void addEdges(int edgeCount) {
    int u, v;
    for (int i = 0; i < edgeCount; i++) {
        cin >> u >> v;
        graph[u - 1].edges.push_back(v - 1);
        graph[v - 1].edges.push_back(u - 1);
    }
}

int main() {
    int nodeCount, edgeCount;
    cin >> nodeCount >> edgeCount;

    initializeGraph(nodeCount);
    addEdges(edgeCount);

    for (int i = 0; i < nodeCount; i++) {
        if (!visited[i]) {
            colorGraph(i, 1);
        }
    }

    for (int i = 0; i < nodeCount; i++) {
        cout << graph[i].color << " ";
    }
    cout << endl;

    return 0;
}