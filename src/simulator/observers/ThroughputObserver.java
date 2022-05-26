package simulator.observers;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import simulator.protocols.CausalityProtocol;

public class ThroughputObserver implements Control {

	public ThroughputObserver(String prefix) {}

	@Override
	public boolean execute() {
		System.err.println(CommonState.getTime() + ": " + this.getClass().getName() + " extracting node throughput.");

		for (int i = 0; i < Network.size(); i++) {
			var node = Network.get(i);
			var protocol = (CausalityProtocol) node.getProtocol(Configuration.lookupPid(CausalityProtocol.protName));
			var executedOperations = protocol.getExecutedOperations();
			System.out.println("throughput-node-" + i + ": " + executedOperations);
		}

		System.out.println();
		return false;
	}
}
