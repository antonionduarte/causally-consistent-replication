import matrix_utils
import random

def generate_tree(matrix):
	mat = matrix_utils.matrix_from_file(matrix)
	n_nodes = len(mat)
	random_node = random.randint(0, n_nodes - 1)

	adjacency = []

	i = 0
	while i < len(matrix):
		
		i = i + 1



def main():
	generate_tree('../config/latencies/latency-10-mat.txt')


main()