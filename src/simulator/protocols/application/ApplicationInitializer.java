package simulator.protocols.application;

import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

/**
 * Responsible for Initializing the application which consists on running startClients.
 */
public class ApplicationInitializer implements Control {

	public ApplicationInitializer(String prefix) {
	}

	@Override
	public boolean execute() {
		for (int i = 0; i < Network.size(); i++) {
			Node node = Network.get(i);
			((ApplicationProtocol) node.getProtocol(ApplicationProtocol.pid)).startClients(node);
		}
		return false;
	}
}
