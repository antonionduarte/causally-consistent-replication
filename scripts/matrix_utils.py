def matrix_from_file(path):
	input_matrix = open(path, 'r')
	input_lines = input_matrix.readlines()
	input_matrix.close()
	matrix = []

	for line in input_lines:
		stripped = line.strip()
		processed = stripped.split(',')
		processed.pop()
		matrix.append(processed)

	return matrix