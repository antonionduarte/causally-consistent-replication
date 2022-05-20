package simulator.observers;

import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import simulator.protocols.CausalityProtocol;
import simulator.protocols.application.ApplicationProtocol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThroughputObserver implements Control {
	/**
	 * Map nodeID -> Throughput.
	 */
	private final Map<Long, Long> throughputPerNode;

	public ThroughputObserver() {
		this.throughputPerNode = new HashMap<>();
	}

	@Override
	public boolean execute() {
		System.err.println(CommonState.getTime() + ": " + this.getClass().getName() + " extracting node throughput.");

		for (int i = 0; i < Network.size(); i++) {
			var node = Network.get(i);
			var protocol = (CausalityProtocol) node.getProtocol(CausalityProtocol.causalityPid);
			var executedOperations = protocol.getExecutedOperations();
			throughputPerNode.put(node.getID(), executedOperations);
		}

		writeToFile();
		return false;
	}

	/**
	 * Writes the collected statistics in latencyPerNode to a file.
	 */
	private void writeToFile() {
		for (long nodeId : throughputPerNode.keySet()) {
			var totalOps = throughputPerNode.get(nodeId);
			System.out.print("Node: " + nodeId);
			System.out.println("," + totalOps);
			System.out.println();
		}
	}
}
