package simulator.node;

import peersim.config.Configuration;
import peersim.core.GeneralNode;
import peersim.core.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class PartitionsNode extends GeneralNode {

	private static final String PAR_PARTITIONS_PATH = "partitions_file";

	/**
	 * Map from nodeID -> List of Character,
	 * where each Character represents a Partition.
	 */
	private final Map<Long, List<Character>> partitions;

	/**
	 * Used to construct the prototype node. This class currently does not
	 * have specific configuration parameters and so the parameter
	 * <code>prefix</code> is not used. It reads the protocol components
	 * (components that have type {@value Node#PAR_PROT}) from
	 * the configuration.
	 */
	public PartitionsNode(String prefix) {
		super(prefix);
		this.partitions = new HashMap<>();
		var path = Configuration.getString(prefix + "." + PAR_PARTITIONS_PATH);
		var file = new File(path);
		this.parsePartitions(file);
	}

	@Override
	public Object clone() {
		return super.clone();
	}

	public Map<Long, List<Character>> getPartitions() {
		return partitions;
	}

	public List<Character> getNodePartitions(long nodeId) {
		return partitions.get(nodeId);
	}

	private void parsePartitions(File file) {
		// read partitions from the specified file
		try (var scanner = new Scanner(file)) {
			// parse the file
			var nodeCounter = 0L;

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] partitions = line.split(",");
				List<Character> partitionList = new ArrayList<>(partitions.length);

				for (var partition : partitions) {
					partitionList.add(partition.charAt(0));
				}

				this.partitions.put(nodeCounter, partitionList);

				nodeCounter++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}