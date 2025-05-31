package main

import (
    "fmt"
)

type Node struct {
    n     int
    edges []int
}

type Component struct {
    size  int
    nodes []int
}

var graph []*Node
var components []Component
var mark []bool

func dfs(u int) {
    mark[u] = true
    components[len(components)-1].size++
    components[len(components)-1].nodes = append(components[len(components)-1].nodes, u)
    for _, v := range graph[u].edges {
        if !mark[v] {
            dfs(v)
        }
    }
}

func main() {
    var n, m int
    fmt.Scan(&n, &m)

    graph = make([]*Node, n)
    mark = make([]bool, n)

    for i := 0; i < n; i++ {
        graph[i] = &Node{n: i}
    }

    for i := 0; i < m; i++ {
        var u, v int
        fmt.Scan(&u, &v)
        graph[u-1].edges = append(graph[u-1].edges, v-1)
        graph[v-1].edges = append(graph[v-1].edges, u-1)
    }

    for i := 0; i < n; i++ {
        if !mark[i] {
            components = append(components, Component{})
            dfs(i)
        }
    }

    fmt.Println(len(components))
    for _, c := range components {
        fmt.Println(c.size)
        for _, i := range c.nodes {
            fmt.Printf("%d ", i+1)
        }
        fmt.Println()
    }
}