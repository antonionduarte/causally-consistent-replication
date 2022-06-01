"""
Processes a Matrix directly copied from https://www.cloudping.co/grid
And puts it into a csv like format
"""

def process_mat(input, out):
	file = open(input, 'r')
	lines = file.readlines()
	file.close()

	final = []

	for line in lines:
		stripped = line.strip()
		processed = stripped.split('\t')
		final.append(processed)

	file = open(out, 'w')

	for line in final:
		for latency in line:
			file.write(str(latency) + ',')
		file.write('\n')

	file.close()


def main():
	process_mat('../config/latencies/latency-mat.txt', '../config/latencies/matrix.txt')


main()