package causalityprotocols.c3;

import peersim.core.Node;
import simulator.protocols.CausalityProtocol;
import simulator.protocols.messages.Message;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	public void uponOperationExecuted(Node node, Message message) {
		C3Message c3Message = (C3Message) message.getProtocolMessage();

		// previous writes are still executing
		if (executedClock.get(message.getOriginNode().getID()) + 1 != c3Message.getLblId()) {
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

	}

	@Override
	public void uponOperationExecuting(Node node, Message message) {
		// if the message is from a local client/datastore, do nothing
		var currentClock = this.executingClock.get(message.getOriginNode().getID());
		this.executingClock.put(message.getOriginNode().getID(), currentClock + 1);
	}

	/**
	 * Check if subsequent operations already completed.
	 *
	 * @param originNode The originNode.
	 * @param toCheck The list of subsequent operations to check.
	 */
	private void checkAheadOps(Node originNode, List<Long> toCheck) {
		for (var lblId : toCheck) {
			var nodeClock = this.executedClock.get(originNode.getID());
			if (nodeClock + 1 == lblId) {
				this.executedClock.put(originNode.getID(), lblId);
				toCheck.remove(lblId);
			}
		}
	}
}
