package causalityprotocols.saturn;

import peersim.core.Node;
import simulator.protocols.causality.CausalityProtocol;
import simulator.protocols.messages.Message;

public class Saturn extends CausalityProtocol {

	private boolean operationExecuting;

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
	public boolean checkCausality(Node node, Message message) {
		if (message.getOperationType() == Message.OperationType.READ) {
			return true;
		}
		if (message.getOriginNode().getID() == node.getID()) {
			return true;
		}
		return !operationExecuting;
	}

	@Override
	public void operationFinishedExecution(Node node, Message message) {
		if (message.getOriginNode().getID() != node.getID())
			this.operationExecuting = false;
	}

	@Override
	public void operationStartedExecution(Node node, Message message) {
		if (message.getOriginNode().getID() != node.getID())
			this.operationExecuting = true;
	}
}
