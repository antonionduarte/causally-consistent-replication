package simulator.observers.latency;

import peersim.core.Control;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import simulator.protocols.application.ApplicationProtocol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Runs in the end of the simulation,
 * retrieves the Client perceived Latency statistics.
 */
public class LatencyObserver implements Control {

	/**
	 * Map nodeID -> List of latency observations.
	 */
	private final Map<Long, List<Long>> latencyPerNode;

	public LatencyObserver() {
		this.latencyPerNode = new HashMap<>();
	}

	@Override
	public boolean execute() {
		System.err.println(CommonState.getTime() + ": " + this.getClass().getName() + " extracting client perceived Latencies.");

		for (int i = 0; i < Network.size(); i++) {
			var node = Network.get(i);
			var application = (ApplicationProtocol) node.getProtocol(ApplicationProtocol.applicationPid);
			var nodeLatencies = application.getMessageLatencies();
			latencyPerNode.put(node.getID(), nodeLatencies);
		}

		writeToFile();
		return false;
	}

	/**
	 * Writes the collected statistics in latencyPerNode to a file.
	 */
	private void writeToFile() {
		for (long nodeId : latencyPerNode.keySet()) {
			var latencies = latencyPerNode.get(nodeId);
			System.out.print("Node: " + nodeId);
			var toPrint = new StringBuilder(nodeId + ",");
			for (long latency : latencies) {
				toPrint.append(latency).append(",");
			}
			System.out.print(toPrint);
			System.out.println();
		}
	}
}
