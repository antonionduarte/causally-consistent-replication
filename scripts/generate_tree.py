import matrix_utils
import random

""" 
matrix  - the latency matrix
k       - replication factor
"""
def generate_tree(matrix, k):
    mat = matrix_utils.matrix_from_file(matrix)
    n_nodes = len(mat)
    random_node = random.randint(0, n_nodes - 1)

    adjacency_mat = {}
    final_adjacency_mat = {}

    # build all-to-all graph
    i = 0
    while i < len(mat):
        adjacency_mat[i] = []
        j = 0
        while j < len(mat):
            if j != i:
                adjacency_mat[i].append((j, float(mat[i][j])))
            j = j + 1
        i = i + 1

    # build "optimized" graph
    visited = []
    to_visit = []
    to_visit.append(random_node)

    print(adjacency_mat)
    print()
    print('random_node: ' + str(random_node))
    print()

    while len(to_visit) > 0:
        node = to_visit.pop()
        visited.append(node)

        neighbours = [(-1, float('inf'))] * k

        for edge in adjacency_mat[node]:
            j = 0
            while j < len(neighbours):
                if not visited.count(edge[0]) > 0:
                    if edge[0] < neighbours[j][1]:
                        neighbours[j] = edge
                j = j + 1

        for elem in neighbours:
            if elem[0] != -1:
                visited.append(elem[0])
                to_visit.append(elem[0])
            else:
                neighbours.remove(elem)

        final_adjacency_mat[node] = neighbours
    print(final_adjacency_mat)
    

def main():
	generate_tree('../config/latencies/latency-10-mat.txt', 3)


main()
