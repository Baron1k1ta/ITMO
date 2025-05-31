package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
)

type Node struct {
	component    int
	edges        []int
	reverseEdges []int
}

var graph []Node
var sequence []int
var visited []bool

func explore(node int) {
	visited[node] = true
	for _, neighbor := range graph[node].edges {
		if !visited[neighbor] {
			explore(neighbor)
		}
	}
	sequence = append(sequence, node)
}

func assignComponent(node, comp int) {
	visited[node] = true
	graph[node].component = comp
	for _, neighbor := range graph[node].reverseEdges {
		if !visited[neighbor] {
			assignComponent(neighbor, comp)
		}
	}
}

func main() {
	reader := bufio.NewReader(os.Stdin)
	writer := bufio.NewWriter(os.Stdout)
	defer writer.Flush()

	line, _ := reader.ReadString('\n')
	parts := strings.Fields(line)
	n, _ := strconv.Atoi(parts[0])
	m, _ := strconv.Atoi(parts[1])

	graph = make([]Node, n)
	visited = make([]bool, n)

	for i := 0; i < m; i++ {
		line, _ = reader.ReadString('\n')
		parts = strings.Fields(line)
		u, _ := strconv.Atoi(parts[0])
		v, _ := strconv.Atoi(parts[1])
		u--
		v--
		graph[u].edges = append(graph[u].edges, v)
		graph[v].reverseEdges = append(graph[v].reverseEdges, u)
	}

	for i := 0; i < n; i++ {
		if !visited[i] {
			explore(i)
		}
	}

	for i, j := 0, len(sequence)-1; i < j; i, j = i+1, j-1 {
		sequence[i], sequence[j] = sequence[j], sequence[i]
	}

	for i := range visited {
		visited[i] = false
	}

	component := 0
	for _, node := range sequence {
		if !visited[node] {
			component++
			assignComponent(node, component)
		}
	}

	fmt.Fprintln(writer, component)
	for i := 0; i < n; i++ {
		fmt.Fprint(writer, graph[i].component, " ")
	}
	fmt.Fprintln(writer)
}