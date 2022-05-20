package simulator.observers;

import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import simulator.protocols.CausalityProtocol;

import java.util.*;

public class VisibilityObserver implements Control {

	Map<String, List<Long>> visibilityTimes;

	public VisibilityObserver() {
		this.visibilityTimes = new HashMap<>();
	}

	@Override
	public boolean execute() {
		System.err.println(CommonState.getTime() + ": " + this.getClass().getName() + " extracting Message Visibility Times.");

		for (int i = 0; i < Network.size(); i++) {
			var node = Network.get(i);
			var protocol = (CausalityProtocol) node.getProtocol(CausalityProtocol.causalityPid);
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

		writeToFile();
		return false;
	}

	/**
	 * Writes the collected statistics in visibilityTimes to a file.
	 */
	private void writeToFile() {
		for (var messageId : visibilityTimes.keySet()) {
			String toPrint = messageId;

			for (var visibility : visibilityTimes.get(messageId)) {
				toPrint = "," + visibility;
			}

			System.out.println(toPrint);
			System.out.println();
		}
	}
}
