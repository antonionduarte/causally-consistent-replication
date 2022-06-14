package causalityprotocols.c3;

import peersim.core.CommonState;
import peersim.core.Node;
import simulator.protocols.CausalityProtocol;
import simulator.protocols.messages.Message;

import java.util.*;

public class C3 extends CausalityProtocol {

	/**
	 * Maps from nodeId -> clockValue
	 */
	private Map<Long, Long> executingClock;
	private Map<Long, Long> executedClock;

	/**
	 * Map from nodeId -> lblId
	 */
	private Map<Long, List<Long>> aheadExecutedOps;

	// waitingOps not necessary because it's handled by CausalityProtocol

	private long writeCounter; // probably not necessary


	/**
	 * The constructor for the protocol.
	 */
	public C3(String prefix) {
		super(prefix);
	}

	@Override
	public Object clone() {
		C3 clone = (C3) super.clone();
		clone.executedClock = new HashMap<>();
		clone.executingClock = new HashMap<>();
		clone.aheadExecutedOps = new HashMap<>();
		clone.writeCounter = -1;
		return clone;
	}

	@Override
	public boolean checkCausality(Node node, Message message) {
		if (message.getOperationType() == Message.OperationType.READ) {
			return true;
		}

		C3Message wrappedMessage = (C3Message) message.getProtocolMessage();

		// Means it came from local DS
		if (wrappedMessage == null) {
			this.writeCounter++;
			message.setProtocolMessage(new C3Message(new HashMap<>(), writeCounter));
			C3Message wrapped = (C3Message) message.getProtocolMessage();
			wrapped.getLblDeps().putAll(executingClock);
			return false;
		}

		Map<Long, Long> messageDeps = wrappedMessage.getLblDeps();

		for (long nodeId : messageDeps.keySet()) {
			if (!executedClock.containsKey(nodeId)) {
				executedClock.put(nodeId, 0L);
			}

			if (executedClock.get(nodeId) < messageDeps.get(nodeId)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void operationFinishedExecution(Node node, Message message) {
		if (message.getOperationType() == Message.OperationType.READ) {
			return;
		}

		C3Message c3Message = (C3Message) message.getProtocolMessage();
		var executedState = executedClock.get(message.getOriginNode().getID());
		// Previous writes are still executing
		if (executedState == null || (executedState + 1 != c3Message.getLblId())) {
			if (aheadExecutedOps.containsKey(message.getOriginNode().getID())) {
				aheadExecutedOps.get(message.getOriginNode().getID()).add(c3Message.getLblId());
			}
			else {
				List<Long> nodeAheadExecutedOps = new LinkedList<>();
				nodeAheadExecutedOps.add(c3Message.getLblId());
				aheadExecutedOps.put(message.getOriginNode().getID(), nodeAheadExecutedOps);
			}
		}
		else {
			executedClock.put(message.getOriginNode().getID(), c3Message.getLblId());
			if (aheadExecutedOps.containsKey(message.getOriginNode().getID())) {
				this.checkAheadOps(
						message.getOriginNode(),
						aheadExecutedOps.get(message.getOriginNode().getID())
				);
			}
		}

		System.out.println("DEBUG - Time:" + CommonState.getTime() + " - Executed - : " + message.getMessageId() + " - Node:" + CommonState.getNode().getID());
		System.out.println("Executed Clock - " + this.executedClock);
		System.out.println("Executing Clock - " + this.executingClock);
		System.out.println();
	}

	@Override
	public void operationStartedExecution(Node node, Message message) {
		if (message.getOperationType() == Message.OperationType.READ) {
			return;
		}

		// If the message is from a local client/datastore, do nothing
		var time = CommonState.getTime();
		var lblDeps = ((C3Message) message.getProtocolMessage()).getLblDeps();
		var currentClock = this.executingClock.get(message.getOriginNode().getID());
		if (currentClock == null) this.executingClock.put(message.getOriginNode().getID(), 0L); // TODO: Wrong?
		else this.executingClock.put(message.getOriginNode().getID(), currentClock + 1);

		System.out.println("DEBUG - Time:" + CommonState.getTime() + " - Executing - : " + message.getMessageId() + " - Node:" + CommonState.getNode().getID());
		System.out.println("Executed Clock - " + this.executedClock);
		System.out.println("Executing Clock - " + this.executingClock);
		System.out.println();
	}

	/**z
	 * Check if subsequent operations already completed.
	 *
	 * @param originNode The originNode.
	 * @param toCheck The list of subsequent operations to check.
	 */
	private void checkAheadOps(Node originNode, List<Long> toCheck) {
		var toRemove = new ArrayList<Long>();
		for (var lblId : toCheck) {
			var nodeClock = this.executedClock.get(originNode.getID());
			if (nodeClock + 1 == lblId) {
				this.executedClock.put(originNode.getID(), lblId);
				toRemove.add(lblId);
			}
		}
		toCheck.removeAll(toRemove);
	}
}
