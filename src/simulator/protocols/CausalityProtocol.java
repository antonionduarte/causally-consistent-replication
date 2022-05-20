package simulator.protocols;

import simulator.protocols.application.ApplicationProtocol;
import simulator.protocols.broadcast.Broadcast;
import simulator.protocols.broadcast.BroadcastProtocol;
import simulator.protocols.messages.Message;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDSimulator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public abstract class CausalityProtocol implements Causality {

	/**
	 * The execution time for an operation.
	 */
	private final int writeTime;
	private final int readTime;

	/**
	 * Event Queue, saves the events that weren't able to be processed due to issues with causality.
	 */
	private Queue<Message> messageQueue;

	public static int causalityPid;

	/**
	 * Statistic collection structure - Visibility times.
	 */
	private Map<String, Long> visibilityTimes;

	/**
	 * The total amount of executed operations within this node.
	 */
	private long executedOperations;

	/**
	 * The constructor for the protocol.
	 */
	public CausalityProtocol(String prefix) {
		causalityPid = Configuration.getPid(prefix);

		this.writeTime = Configuration.getInt("WRITE_TIME");
		this.readTime = Configuration.getInt("READ_TIME");
	}

	@Override
	public Object clone() {
		try {
			CausalityProtocol clone = (CausalityProtocol) super.clone();
			clone.executedOperations = 0;
			clone.messageQueue = new LinkedList<>();
			clone.visibilityTimes = new HashMap<>();
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void processEvent(Node node, int pid, Object event) {
		Message message = (Message) event;

		// could throw NPE if not well verified within the protocol
		if (verifyCausality(node, message)) {
			if (message.isPropagating()) {
				message.togglePropagating();
				executeOperation(node, message, pid);
				uponMessageExecuting(node, message);
			}
			else {
				this.visibilityTimes.put(message.getMessageId(), CommonState.getTime());
				this.executedOperations++;
				uponMessageExecuted(node, message);

				if (message.getMessageType() == Message.MessageType.WRITE) {
					Broadcast broadcast = (Broadcast) node.getProtocol(BroadcastProtocol.broadcastPid);
					broadcast.broadcastMessage(node, message);
				}

				if (message.getOriginNode().getID() == node.getID()) {
					EDSimulator.add(0, event, node, ApplicationProtocol.applicationPid);
				}

				processQueue(node, pid);
			}
		} else {
			this.messageQueue.add(message);
		}
	}

	@Override
	public void processQueue(Node node, int pid) {
		for (Message message : messageQueue) {
			if (verifyCausality(node, message)) {
				this.messageQueue.remove(message);
				executeOperation(node, message, pid);
			}
		}
	}

	@Override
	public void executeOperation(Node node, Message message, int pid) {
		long expectedArrivalTime;
		uponMessageExecuting(node, message);

		if (message.getMessageType() == Message.MessageType.READ) {
			expectedArrivalTime = readTime;
		} else {
			expectedArrivalTime = writeTime;
		}

		EDSimulator.add(expectedArrivalTime, message, node, pid);
	}

	/**
	 * @return The time at which a message was made visible within this node.
	 */
	public Map<String, Long> getVisibilityTimes() {
		return visibilityTimes;
	}

	/**
	 * @return The total amount of executed operations within the node.
	 */
	public long getExecutedOperations() {
		return executedOperations;
	}

	@Override
	public abstract boolean verifyCausality(Node node, Message message);

	@Override
	public abstract void uponMessageExecuted(Node node, Message message);

	@Override
	public abstract void uponMessageExecuting(Node node, Message message);


}
