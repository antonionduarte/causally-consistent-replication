package simulator.observers;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import simulator.protocols.application.ApplicationProtocol;

/**
 * Runs in the end of the simulation,
 * retrieves the Client perceived Latency statistics.
 *
 * TODO: Fix latency observations
 */
public class LatencyObserver implements Control {

	public LatencyObserver(String prefix) {}

	@Override
	public boolean execute() {
		System.err.println(CommonState.getTime() + ": " + this.getClass().getName() + " extracting client perceived Latencies.");

		for (int i = 0; i < Network.size(); i++) {
			var node = Network.get(i);
			var application = (ApplicationProtocol) node.getProtocol(Configuration.lookupPid(ApplicationProtocol.protName));
			var nodeLatencies = application.getMessageLatencies();
			System.out.print("perceived-latency-node-" + i + ": ");

			for (var latency : nodeLatencies) {
				System.out.print(latency + ",");
			}

			System.out.println();
		}

		return false;
	}
}
