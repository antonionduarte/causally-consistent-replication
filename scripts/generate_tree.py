import matrix_utils
import random
import copy

""" 
matrix  - the latency matrix
k       - replication factor
"""
def generate_tree(matrix, k):
	mat = matrix_utils.matrix_from_file(matrix)
	n_nodes = len(mat)
	random_node = random.randint(0, n_nodes - 1)

	adjacency_mat = {}
	tree = {}
	final_tree = {}
	mst_set = []
	num_connections = {}

	# build all-to-all graph
	i = 0
	while i < len(mat):
		adjacency_mat[i] = []
		j = 0
		while j < len(mat):
			if j != i:
				adjacency_mat[i].append((i, float(mat[i][j]), j))
			j = j + 1
		tree[i] = []
		final_tree[i] = []
		i = i + 1

	mst_set.append(random_node)

	# generate the tree without symmetric-connections (with symmetric i don't mean necessarily same weight)
	while len(mst_set) != len(adjacency_mat):
		# select next minimum connection that isn't in the list of edges
		min_edge = (-1, float('inf'), -1)
		for vert in mst_set:
			for edge in adjacency_mat[vert]:
				if (edge[1] < min_edge[1]) and (mst_set.count(edge[2]) == 0) and (len(tree[vert]) < k):
					min_edge = edge

		if min_edge[0] != -1:
			mst_set.append(min_edge[2])
			tree[min_edge[0]].append(min_edge)
			
	for vert in tree:
		for edge in tree[vert]:
			final_tree[vert].append(edge)
			final_tree[edge[2]].append((edge[2], mat[edge[2]][edge[0]], edge[0]))

	return final_tree




#def tree_to_file(tree):

	

def main():
	tree = generate_tree('../config/latencies/latency-10-mat.txt', 2)
	print(tree)

main()