package main

import (
    "fmt"
)

type Node struct {
    depth int
    up    int
    edges []int
}

var graph []Node
var visited []bool
var result setOfInts
var n int

type setOfInts map[int]struct{}

func newSet() setOfInts {
    return make(setOfInts)
}

func (s setOfInts) add(value int) {
    s[value] = struct{}{}
}

func dfs(vertex, parent, depth int) {
    graph[vertex].depth = depth
    graph[vertex].up = depth
    visited[vertex] = true
    childCount := 0
    for _, neighbor := range graph[vertex].edges {
        if neighbor == parent {
            continue
        }
        if !visited[neighbor] {
            dfs(neighbor, vertex, depth+1)
            graph[vertex].up = min(graph[vertex].up, graph[neighbor].up)
            if graph[neighbor].up >= graph[vertex].depth && parent != -1 && vertex >= n {
                result.add(vertex)
            }
            childCount++
        } else {
            graph[vertex].up = min(graph[vertex].up, graph[neighbor].depth)
        }
    }
    if childCount > 1 && parent == -1 && vertex >= n {
        result.add(vertex)
    }
}

func min(a, b int) int {
    if a < b {
        return a
    }
    return b
}

func main() {
    fmt.Scan(&n)
    var m int
    fmt.Scan(&m)

    graph = make([]Node, n+m)
    visited = make([]bool, n+m)
    result = newSet()

    for i := 0; i < m; i++ {
        var vertices [3]int
        for j := range vertices {
            fmt.Scan(&vertices[j])
            vertices[j]--
            graph[n+i].edges = append(graph[n+i].edges, vertices[j])
            graph[vertices[j]].edges = append(graph[vertices[j]].edges, n+i)
        }
    }

    for i := 0; i < n; i++ {
        if !visited[i] {
            dfs(i, -1, 0)
        }
    }

    fmt.Println(len(result))
    for key := range result {
        fmt.Print(key + 1 - n, " ")
    }
}