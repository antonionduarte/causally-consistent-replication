package simulator.node;

import peersim.config.Configuration;
import peersim.core.GeneralNode;
import peersim.core.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Array;
import java.util.*;

public class PartitionsNode extends GeneralNode {

	private static final String PAR_PARTITIONS_PATH = "partitions_file";

	private final Map<Long, List<Character>> partitions;
	private final List<Character> distinctPartitions;

	/**
	 * Used to construct the prototype node. This class currently does not
	 * have specific configuration parameters and so the parameter
	 * <code>prefix</code> is not used. It reads the protocol components
	 * (components that have type {@value Node#PAR_PROT}) from
	 * the configuration.
	 */
	public PartitionsNode(String prefix) {
		super(prefix);
		var path = Configuration.getString(prefix + "." + PAR_PARTITIONS_PATH);
		this.partitions = parsePartitions(path);
		this.distinctPartitions = new ArrayList<>();

		for (var partitionList : partitions.values()) {
			for (var partition : partitionList) {
				if (!distinctPartitions.contains(partition)) {
					this.distinctPartitions.add(partition);
				}
			}
		}
	}

	@Override
	public Object clone() {
		return super.clone();
	}

	public List<Character> getDistinctPartitions() {
		return distinctPartitions;
	}

	public Map<Long, List<Character>> getAllPartitions() {
		return partitions;
	}

	/**
	 * @return The partition list of the current node.
	 */
	public List<Character> getPartitions() {
		return partitions.get(this.getID());
	}

	/**
	 * @param nodeId The id of the node to get the partition list of.
	 * @return The partition list of the node identified by nodeId.
	 */
	public List<Character> getPartitions(long nodeId) {
		return partitions.get(nodeId);
	}

	private static Map<Long, List<Character>> parsePartitions(String path) {
		// read partitions from the specified file
		var partitions = new HashMap<Long, List<Character>>();
		var file = new File(path);
		try (var scanner = new Scanner(file)) {
			// parse the file
			var nodeCounter = 0L;

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] partitionsList = line.split(",");
				List<Character> partitionList = new ArrayList<>(partitionsList.length);

				for (var partition : partitionsList) {
					partitionList.add(partition.charAt(0));
				}

				partitions.put(nodeCounter, partitionList);
				nodeCounter++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return partitions;
	}
}