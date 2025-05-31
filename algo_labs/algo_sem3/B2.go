package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
)

type GraphNode struct {
	neighbors      []int
	reverseNeighbors []int
}

var graph []GraphNode
var sequence []int
var visited []bool
var size int

func explore(node int) {
	visited[node] = true
	for _, neighbor := range graph[node].neighbors {
		if !visited[neighbor] {
			explore(neighbor)
		}
	}
	sequence = append(sequence, node)
}

func traverse(node int) {
	visited[node] = true
	size++
	for _, neighbor := range graph[node].reverseNeighbors {
		if !visited[neighbor] {
			traverse(neighbor)
		}
	}
}

func main() {
	reader := bufio.NewReader(os.Stdin)
	line, _ := reader.ReadString('\n')
	input := strings.Split(strings.TrimSpace(line), " ")
	n, _ := strconv.Atoi(input[0])
	m, _ := strconv.Atoi(input[1])

	graph = make([]GraphNode, n)
	visited = make([]bool, n)

	for i := 0; i < m; i++ {
		line, _ := reader.ReadString('\n')
		parts := strings.Split(strings.TrimSpace(line), " ")
		u, _ := strconv.Atoi(parts[0])
		v, _ := strconv.Atoi(parts[1])
		if u == v {
			continue
		}
		u--
		v--
		graph[v].neighbors = append(graph[v].neighbors, u)
		graph[u].reverseNeighbors = append(graph[u].reverseNeighbors, v)
	}

	for i := 0; i < n; i++ {
		if !visited[i] {
			explore(i)
		}
	}

	for i := 0; i < n/2; i++ {
		sequence[i], sequence[n-i-1] = sequence[n-i-1], sequence[i]
	}

	for i := range visited {
		visited[i] = false
	}

	result := 0
	for _, node := range sequence {
		if !visited[node] {
			result++
			size = 0
			traverse(node)
			if size > 1 {
				result++
			}
		}
	}

	fmt.Println(result)
}