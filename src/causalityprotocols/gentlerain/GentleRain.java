package causalityprotocols.gentlerain;

import peersim.core.Node;
import simulator.protocols.causality.CausalityProtocol;
import simulator.protocols.messages.Message;

public class GentleRain extends CausalityProtocol {

	/**
	 * The constructor for the protocol.
	 *
	 * @param prefix
	 */
	public GentleRain(String prefix) {
		super(prefix);
	}

	@Override
	public boolean checkCausality(Node node, Message message) {
		return false;
	}

	@Override
	public void operationFinishedExecution(Node node, Message message) {

	}

	@Override
	public void operationStartedExecution(Node node, Message message) {

	}
}
