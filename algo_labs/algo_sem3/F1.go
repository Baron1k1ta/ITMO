package main

import (
    "fmt"
    "os"
)

type Node struct {
    degree int
    edges  []int
}

var graph []Node
var visited []int

func main() {
    var numNodes, numEdges int
    fmt.Scan(&numNodes, &numEdges)

    graph = make([]Node, numNodes)
    visited = make([]int, numNodes)

    for i := 0; i < numEdges; i++ {
        var from, to int
        fmt.Scan(&from, &to)
        graph[from-1].edges = append(graph[from-1].edges, to-1)
        graph[to-1].degree++
    }

    for i := 0; i < numNodes; i++ {
        if visited[i] == 0 {
            if !depthFirstSearch(i) {
                fmt.Println(-1)
                os.Exit(0)
            }
        }
    }

    queue := make([]int, 0)
    for i := 0; i < numNodes; i++ {
        if graph[i].degree == 0 {
            queue = append(queue, i)
        }
    }

    performTopologicalSorting(queue)
}

func depthFirstSearch(nodeIndex int) bool {
    visited[nodeIndex] = 1
    for _, neighbor := range graph[nodeIndex].edges {
        if visited[neighbor] == 0 {
            if !depthFirstSearch(neighbor) {
                return false
            }
        } else if visited[neighbor] == 1 {
            return false
        }
    }
    visited[nodeIndex] = 2
    return true
}

func performTopologicalSorting(queue []int) {
    for len(queue) > 0 {
        v := queue[0]
        fmt.Print(v + 1, " ")
        queue = queue[1:]

        for _, neighbor := range graph[v].edges {
            graph[neighbor].degree--
            if graph[neighbor].degree == 0 {
                queue = append(queue, neighbor)
            }
        }
    }
}