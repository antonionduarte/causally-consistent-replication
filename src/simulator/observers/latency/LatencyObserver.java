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
	private Map<Long, List<Long>> latencyPerNode;

	public LatencyObserver() {
		this.latencyPerNode = new HashMap<>();
	}

	@Override
	public boolean execute() {
		System.err.println(CommonState.getTime() + ": " + this.getClass().getName() + " extracting client perceived Latencies.");

		for (int i = 0; i < Network.size(); i++) {
			Node node = Network.get(i);
			ApplicationProtocol application = (ApplicationProtocol) node.getProtocol(ApplicationProtocol.applicationPid);
			List<Long> nodeLatencies = application.getMessageLatencies();
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
			List<Long> latencies = latencyPerNode.get(nodeId);
			System.out.print("Node: " + nodeId);
			StringBuilder toPrint = new StringBuilder(nodeId + ",");
			for (long latency : latencies) {
				toPrint.append(latency).append(",");
			}
			System.out.print(toPrint);
			System.out.println();
		}
	}
}
