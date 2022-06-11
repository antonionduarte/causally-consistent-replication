package simulator.transports.utils;

import java.io.File;
import java.util.Scanner;

public class ReadLatencyMatrix {

	public static long[][] readLatencyMatrix(String path) {
		long[][] matrix = null;

		try {
			File file = new File(path);
			Scanner scanner = new Scanner(file);

			int i = 0;
			while (scanner.hasNextLine()) {
				String data = scanner.nextLine();
				var splitData = data.split(",");

				if (matrix == null) {
					matrix = new long[splitData.length][splitData.length];
				}

				var j = 0;
				for (var elem : splitData) {
					matrix[i][j] = Math.round(Float.parseFloat(elem));
					j++;
				}

				i++;
			}

			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return matrix;
	}

	public static void main(String[] args) {
		var mat = readLatencyMatrix("config/latencies/latency-0-mat.txt");
	}
}
