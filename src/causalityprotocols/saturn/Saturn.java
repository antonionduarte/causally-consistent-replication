package causalityprotocols.saturn;

import peersim.core.Node;
import simulator.CausalityProtocol;
import simulator.messages.Message;

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
	public boolean verifyCausality(Message message) {
		return false;
	}

	@Override
	public void uponMessageExecuted(Node node, Message message) {

	}

	@Override
	public void uponMessageExecuting(Node node, Message message) {

	}
}
