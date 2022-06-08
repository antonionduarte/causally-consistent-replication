import matrix_utils
import random
import copy

"""
Makes a valid tree graph in the Adjacency-List format from a given latency matrix
and with a max "k" replication factor value.

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


"""
Converts the tree to a format accepted by Peersim's 
WireFromFile format.
"""
def tree_to_file(tree, out):
	file = open(out, 'w')
	for vert in tree:
		file.write(str(vert)) # write vert
		for edge in tree[vert]: # write edges from vert
			file.write(' ' + str(edge[2]))
		file.write('\n')
	file.close() 

	

def main():
	tree = generate_tree('../config/latencies/latency-0-mat.txt', 2)
	tree_to_file(tree, '../config/graphs/tree-graph-0.txt')

main()