package main

import (
    "fmt"
)

type Node struct {
    index int
    edges []int
}

var graph []*Node
var mainNodes []int
var visited []bool

func traverseGraph(nodeIndex int) {
    visited[nodeIndex] = true
    mainNodes[len(mainNodes)-1] = nodeIndex
    for _, neighbor := range graph[nodeIndex].edges {
        if !visited[neighbor] {
            traverseGraph(neighbor)
        }
    }
}

func initializeGraph(n, m int) {
    graph = make([]*Node, n)
    visited = make([]bool, n)

    for i := 0; i < n; i++ {
        graph[i] = &Node{index: i}
    }
}

func readEdges(m int) {
    var u, v int
    for i := 0; i < m; i++ {
        fmt.Scan(&u, &v)
        graph[u-1].edges = append(graph[u-1].edges, v-1)
        graph[v-1].edges = append(graph[v-1].edges, u-1)
    }
}

func main() {
    var nodeCount, edgeCount int
    fmt.Scan(&nodeCount, &edgeCount)

    initializeGraph(nodeCount, edgeCount)
    readEdges(edgeCount)

    for i := 0; i < nodeCount; i++ {
        if !visited[i] {
            mainNodes = append(mainNodes, 0)
            traverseGraph(i)
        }
    }

    fmt.Println(len(mainNodes) - 1)
    for i := 0; i < len(mainNodes)-1; i++ {
        fmt.Printf("%d %d\n", mainNodes[i]+1, mainNodes[i+1]+1)
    }
}