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
	public boolean verifyCausality(Node node, Message message) {
		// System.out.println("DEBUG - Verifying - : " + message.getMessageId() + " - " + CommonState.getNode().getID());

		C3Message wrappedMessage = (C3Message) message.getProtocolMessage();

		// means it came from local DS
		if (wrappedMessage == null) {
			this.writeCounter++;
			message.setProtocolMessage(new C3Message(new HashMap<>(), writeCounter));
			C3Message wrapped = (C3Message) message.getProtocolMessage();
			wrapped.getLblDeps().putAll(executingClock);
			return false;
		}

		Map<Long, Long> messageDeps = wrappedMessage.getLblDeps();

		for (long nodeId : messageDeps.keySet()) {
			if (executedClock.containsKey(nodeId)) {
				if (executedClock.get(nodeId) < messageDeps.get(nodeId)) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public void uponOperationFinishedExecution(Node node, Message message) {
		C3Message c3Message = (C3Message) message.getProtocolMessage();
		var executedState = executedClock.get(message.getOriginNode().getID());
		// previous writes are still executing
		if (executedState != null && (executedState + 1 != c3Message.getLblId())) {
			// System.out.println("TEST 1");
			if (aheadExecutedOps.containsKey(message.getOriginNode().getID())) {
				aheadExecutedOps.get(message.getOriginNode().getID()).add(c3Message.getLblId());
			}
			else {
				// System.out.println("TEST 2");
				List<Long> nodeAheadExecutedOps = new LinkedList<>();
				nodeAheadExecutedOps.add(c3Message.getLblId());
				aheadExecutedOps.put(message.getOriginNode().getID(), nodeAheadExecutedOps);
			}
		}
		else {
			executedClock.put(message.getOriginNode().getID(), c3Message.getLblId());
			// System.out.println("TEST 3");
			if (aheadExecutedOps.containsKey(message.getOriginNode().getID())) {
				// System.out.println("TEST 4");
				this.checkAheadOps(
						message.getOriginNode(),
						aheadExecutedOps.get(message.getOriginNode().getID())
				);
			}
		}

		System.out.println("DEBUG - " + CommonState.getTime() + " Executed - : " + message.getMessageId() + " - " + CommonState.getNode().getID());
		System.out.println("Executed Clock - " + this.executedClock);
		System.out.println("Executing Clock - " + this.executingClock);
		System.out.println();
	}

	@Override
	public void uponOperationExecuted(Node node, Message message) {
		// if the message is from a local client/datastore, do nothing
		var time = CommonState.getTime();
		var currentClock = this.executingClock.get(message.getOriginNode().getID());
		if (currentClock == null) this.executingClock.put(message.getOriginNode().getID(), 0L);
		else this.executingClock.put(message.getOriginNode().getID(), currentClock + 1);

		System.out.println("DEBUG - " + CommonState.getTime() + " Executing - : " + message.getMessageId() + " - " + CommonState.getNode().getID());
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
