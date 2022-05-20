package causality_protocols.saturn;

import peersim.core.Node;
import simulator.protocols.CausalityProtocol;
import simulator.protocols.messages.Message;

public class Saturn extends CausalityProtocol {

	/**
	 * The constructor for the protocol.
	 */
	public Saturn(String prefix) {
		super(prefix);
	}

	@Override
	public Object clone() {
		return super.clone();
	}

	@Override
	public boolean verifyCausality(Node node, Message message) {
		return true;
	}

	@Override
	public void uponMessageExecuted(Node node, Message message) {

	}

	@Override
	public void uponMessageExecuting(Node node, Message message) {

	}
}
