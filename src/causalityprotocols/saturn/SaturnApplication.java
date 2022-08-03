package causalityprotocols.saturn;

import peersim.core.Node;
import simulator.protocols.application.ApplicationProtocol;
import simulator.protocols.messages.Message;

public class SaturnApplication extends ApplicationProtocol {

	public SaturnApplication(String prefix) {
		super(prefix);
	}

	@Override
	public Object clone() {
		return super.clone();
	}

	@Override
	public void uponReceiveMessage(Node node, Message message) {
		// do nothing
	}

	@Override
	public void changeInitialMessage(Node node, Message message) {
		message.setProtocolMessage(new SaturnMessage(
				node.getID(),
				-1)
		);
	}

	@Override
	public void changeResponseMessage(Node node, Message message) {
		message.setProtocolMessage(new SaturnMessage(
				node.getID(),
				-1)
		);
	}
}
