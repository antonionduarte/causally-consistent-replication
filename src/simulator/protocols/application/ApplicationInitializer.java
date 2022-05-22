package simulator.protocols.application;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

import javax.swing.text.WrappedPlainView;

/**
 * Responsible for Initializing the application which consists on running startClients.
 */
public class ApplicationInitializer implements Control {

	public final String applicationInitializerPrefix;

	public ApplicationInitializer(String prefix) {
		applicationInitializerPrefix = prefix;
	}

	@Override
	public boolean execute() {
		for (int i = 0; i < Network.size(); i++) {
			Node node = Network.get(i);

			((ApplicationProtocol) node.getProtocol(Configuration.lookupPid("application")))
					.startClients(node);
		}
		return false;
	}
}
