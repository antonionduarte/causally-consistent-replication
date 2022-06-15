package simulator.observers;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import simulator.protocols.CausalityProtocol;

import java.util.*;

public class VisibilityObserver implements Control {

	Map<String, List<Long>> visibilityTimes;

	public VisibilityObserver(String prefix) {
		this.visibilityTimes = new HashMap<>();
	}

	@Override
	public boolean execute() {
		System.err.println(CommonState.getTime() + ": " + this.getClass().getName() + " extracting Message Visibility Times.");

		for (int i = 0; i < Network.size(); i++) {
			var node = Network.get(i);
			var protocol = (CausalityProtocol) node.getProtocol(Configuration.lookupPid(CausalityProtocol.protName));
			var messageVisibilities = protocol.getVisibilityTimes();

			for (var messageId : messageVisibilities.keySet()) {
				if (!visibilityTimes.containsKey(messageId)) {
					var visibilityList = new ArrayList<Long>();
					visibilityList.add(messageVisibilities.get(messageId));
					visibilityTimes.put(messageId, visibilityList);
				} else {
					visibilityTimes.get(messageId).add(messageVisibilities.get(messageId));
				}
			}
		}

		return false;
	}
}
