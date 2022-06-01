"""
Processes a matrix and returns a sub-matrix with only
the desired elements from the original one.
"""

def sub_matrix(matrix, indexes, out):
	input_matrix = open(matrix, 'r')
	input_lines = input_matrix.readlines()
	input_matrix.close()
	processed_matrix = []
	matrix = []

	for line in input_lines:
		stripped = line.strip()
		processed = stripped.split(',')
		processed.pop()
		processed_matrix.append(processed)

	for elem in indexes:
		matrix.append([None] * len(indexes))

	i = 0
	while i < len(indexes):
		j = 0
		while j < len(indexes):
			matrix[i][j] = processed_matrix[indexes[i]][indexes[j]]
			j = j + 1
		i = i + 1

	file = open(out, 'w')

	for line in matrix:
		for latency in line:
			file.write(str(latency) + ',')
		file.write('\n')

	file.close()


def main():
	indexes = [0, 19, 7, 8, 14, 13, 16]
	sub_matrix(
		'../config/latencies/processed-latency-matrix.txt',
		indexes,
		'../config/latencies/latency-10-mat.txt'
	)

main()