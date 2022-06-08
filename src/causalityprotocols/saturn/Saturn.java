package causalityprotocols.saturn;

import peersim.core.CommonState;
import peersim.core.Node;
import simulator.protocols.CausalityProtocol;
import simulator.protocols.messages.Message;

public class Saturn extends CausalityProtocol {

	private boolean operationExecuting;

	/**
	 * The constructor for the protocol.
	 */
	public Saturn(String prefix) {
		super(prefix);
	}

	@Override
	public Object clone() {
		Saturn saturn = (Saturn) super.clone();
		saturn.operationExecuting = false;
		return saturn;
	}

	@Override
	public boolean verifyCausality(Node node, Message message) {
		if (message.getMessageType() == Message.MessageType.READ) {
			return true;
		}

		// if there isn't an operation executing, operation can execute.
		return !operationExecuting;
	}

	@Override
	public void uponOperationFinishedExecution(Node node, Message message) {
		this.operationExecuting = false;

		if (message.getMessageType() == Message.MessageType.WRITE) {
			System.out.println("DEBUG - Time:" + CommonState.getTime() + " - Executed - : " + message.getMessageId() + " - Node:" + CommonState.getNode().getID());
			System.out.println();
		}
	}

	@Override
	public void uponOperationExecuted(Node node, Message message) {
		this.operationExecuting = true;

		if (message.getMessageType() == Message.MessageType.WRITE) {
			System.out.println("DEBUG - Time:" + CommonState.getTime() + " - Executing - : " + message.getMessageId() + " - Node:" + CommonState.getNode().getID());
			System.out.println();
		}
	}
}
