package simulator.application;

import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

/**
 * Responsible for Initializing the application which consists on running startClients.
 */
public class ApplicationInitializer implements Control {

	private final int protocolID;

	public ApplicationInitializer(String prefix) {
		this.protocolID = Application.applicationPid;
	}

	@Override
	public boolean execute() {
		for (int i = 0; i < Network.size(); i++) {
			Node node = Network.get(i);
			((Application) node.getProtocol(protocolID)).startClients(node);
		}
		return false;
	}
}
