package causalityprotocols.c3;

import peersim.core.CommonState;
import peersim.core.Node;
import simulator.protocols.causality.CausalityProtocol;
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
		clone.writeCounter = 0;
		return clone;
	}

	@Override
	public boolean checkCausality(Node node, Message message) {
		if (message.getOperationType() == Message.OperationType.READ) {
			return true;
		}

		C3Message wrappedMessage = (C3Message) message.getProtocolMessage();

		if (wrappedMessage == null) {
			this.writeCounter++;
			message.setProtocolMessage(new C3Message(new HashMap<>(), writeCounter, node));
			wrappedMessage = (C3Message) message.getProtocolMessage();
			wrappedMessage.getLblDeps().putAll(executingClock);
		}


		if (message.getOperationType() == Message.OperationType.MIGRATION) {
			return true;
		}

		Map<Long, Long> messageDeps = wrappedMessage.getLblDeps();

		for (long nodeId : messageDeps.keySet()) {
			if (!executedClock.containsKey(nodeId)) {
				executedClock.put(nodeId, 0L);
			}
			if (executedClock.get(nodeId) < messageDeps.get(nodeId)) {
				System.out.println(messageDeps);
				System.out.println("(C3) - Time: " + CommonState.getTime() + " - " + executedClock + " - " + executingClock + " - Node: " + node.getID() + " - Message: " + message.getMessageId() + "\n");
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

		if (message.getOperationType() == Message.OperationType.MIGRATION) {
			return;
		}

		C3Message c3Message = (C3Message) message.getProtocolMessage();
		var executedState = executedClock.computeIfAbsent(c3Message.getOriginNode().getID(), k -> 0L);
		// Previous writes are still executing
		if (executedState + 1 != c3Message.getLblId()) {
			if (aheadExecutedOps.containsKey(c3Message.getOriginNode().getID())) {
				aheadExecutedOps.get(c3Message.getOriginNode().getID()).add(c3Message.getLblId());
			} else {
				List<Long> nodeAheadExecutedOps = new LinkedList<>();
				nodeAheadExecutedOps.add(c3Message.getLblId());
				aheadExecutedOps.put(c3Message.getOriginNode().getID(), nodeAheadExecutedOps);
			}
		} else {
			executedClock.put(c3Message.getOriginNode().getID(), c3Message.getLblId());
			if (aheadExecutedOps.containsKey(c3Message.getOriginNode().getID())) {
				this.checkAheadOps(
						c3Message.getOriginNode(),
						aheadExecutedOps.get(c3Message.getOriginNode().getID())
				);
			}
		}

		System.out.println("(C3) Finish Exec - Node: " + node.getID() + " - mess ID: " + message.getMessageId());
	}

	@Override
	public void operationStartedExecution(Node node, Message message) {
		if (message.getOperationType() == Message.OperationType.READ) {
			return;
		}

		if (message.getOperationType() == Message.OperationType.MIGRATION) {
			return;
		}

		C3Message c3Message = (C3Message) message.getProtocolMessage();
		long currentClock = this.executingClock.computeIfAbsent(c3Message.getOriginNode().getID(), k -> 0L);
		this.executingClock.put(c3Message.getOriginNode().getID(), currentClock + 1);

		System.out.println("(C3) Started Exec - Node: " + node.getID() + " - mess ID: " + message.getMessageId());
	}

	/**
	 * z
	 * Check if subsequent operations already completed.
	 *
	 * @param originNode The originNode.
	 * @param toCheck The list of subsequent operations to check.
	 */
	private void checkAheadOps(Node originNode, List<Long> toCheck) {
		var toRemove = new ArrayList<Long>();
		var didThings = true;
		while (didThings) {
			didThings = false;
			for (var lblId : toCheck) {
				var nodeClock = this.executedClock.get(originNode.getID());
				if (nodeClock + 1 == lblId) {
					this.executedClock.put(originNode.getID(), lblId);
					toRemove.add(lblId);
					didThings = true;
				}
			}
		}
		toCheck.removeAll(toRemove);
	}
}
