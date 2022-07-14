package causalityprotocols.c3;

import peersim.core.Node;
import simulator.protocols.application.ApplicationProtocol;
import simulator.protocols.messages.Message;
import simulator.protocols.messages.ProtocolMessage;

public class C3Application extends ApplicationProtocol {

	public C3Application(String prefix) {
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
		// do nothing
	}

	@Override
	public void changeResponseMessage(Node node, Message message) {
		// do nothing
	}
}
