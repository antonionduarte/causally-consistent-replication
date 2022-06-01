package simulator.protocols;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import simulator.protocols.application.ApplicationProtocol;
import simulator.protocols.broadcast.Broadcast;
import simulator.protocols.broadcast.BroadcastProtocol;
import simulator.protocols.messages.Message;
import simulator.protocols.messages.MessageWrapper;

import java.util.*;

public abstract class CausalityProtocol implements Causality {

	/**
	 * The execution time for an operation.
	 */
	private final int writeTime;
	private final int readTime;

	/**
	 * Event Queue, saves the events that weren't able to be processed due to issues with causality.
	 */
	private Queue<Message> operationQueue;

	public static String protName;

	public static final String WRITE_TIME = "write_time";
	public static final String READ_TIME = "read_time";

	/**
	 * Statistic collection structure - Visibility times.
	 */
	private Map<String, Long> visibilityTimes;
	private Set<String> sentMessages;
	private Set<String> executedMessages;

	/**
	 * The total amount of executed operations within this node.
	 */
	private long executedOperations;

	/**
	 * The constructor for the protocol.
	 */
	public CausalityProtocol(String prefix) {
		protName = (prefix.split("\\."))[1];
		this.writeTime = Configuration.getInt(prefix + "." + WRITE_TIME);
		this.readTime = Configuration.getInt(prefix + "." + READ_TIME);
	}

	@Override
	public Object clone() {
		try {
			CausalityProtocol clone = (CausalityProtocol) super.clone();
			clone.executedOperations = 0;
			clone.operationQueue = new LinkedList<>();
			clone.visibilityTimes = new HashMap<>();
			clone.executedMessages = new HashSet<>();
			clone.sentMessages = new HashSet<>();
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void processEvent(Node node, int pid, Object event) {
		var message = (Message) event;

		// Could throw NPE if not well verified within the protocol
		if (message.isPropagating()) {
			if (verifyCausality(node, message)) {
				System.out.println(
					"DEBUG: Verifies causality - Time:" + CommonState.getTime() + " - " + message.getMessageId() +
					" - Node:" + CommonState.getNode().getID())
				;

				// Message was propagating, and starts executing
				if (message.isPropagating()) {
					message.togglePropagating();
					this.executeOperation(node, message, pid);
				}
			} else {
				System.out.println(
					"DEBUG: Doesn't verify causality - Time:" + CommonState.getTime() + " - " + message.getMessageId() +
					" - Node:" + CommonState.getNode().getID())
				;

				if (!executedMessages.contains(message.getMessageId())) {
					// TODO: Debug - Delete
					//if (message.getMessageId().equals("0_1")) {
					//	System.out.println("TEST TEST TEST - 1");
					//}
					this.operationQueue.add(message);
				}
			}

			// Message was executing
		} else {
			this.visibilityTimes.put(message.getMessageId(), CommonState.getTime());
			this.executedOperations++;

			this.uponOperationFinishedExecution(node, message);

			if (message.getOriginNode().getID() == node.getID()) {
				EDSimulator.add(0, event, node, Configuration.lookupPid(ApplicationProtocol.protName));
			}
		}

		if (!sentMessages.contains(message.getMessageId())) {
			propagateMessage(node, message);
		}

		this.processQueue(node, pid);
	}

	@Override
	public void processQueue(Node node, int pid) {
		var verifiedMessages = new ArrayList<Message>();
		for (Message message : operationQueue) {
			if (verifyCausality(node, message)) {
				verifiedMessages.add(message);
				this.executeOperation(node, message, pid);
			}
		}
		// TODO: Debug - Delete
		//for (var message : verifiedMessages) {
		//	if (message.getMessageId().equals("0_1")) {
		//		System.out.println("TEST TEST TEST - 2");
		//	}
		//}
		operationQueue.removeAll(verifiedMessages);
	}

	@Override
	public void executeOperation(Node node, Message message, int pid) {
		long expectedArrivalTime;
		this.executedMessages.add(message.getMessageId());
		this.uponOperationExecuted(node, message);

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
	public Queue<Message> getOperationQueue() {
		return operationQueue;
	}

	@Override
	public abstract boolean verifyCausality(Node node, Message message);

	@Override
	public abstract void uponOperationFinishedExecution(Node node, Message message);

	@Override
	public abstract void uponOperationExecuted(Node node, Message message);

	private void propagateMessage(Node node, Message message) {
		// TODO: In C3 the messages are propagated before being executed in the local DC
		if (message.getMessageType() == Message.MessageType.WRITE) {
			var broadcast = (Broadcast) node.getProtocol(Configuration.lookupPid(BroadcastProtocol.protName));

			Message toSend = new MessageWrapper(
					message.getMessageType(),
					message.getProtocolMessage(),
					message.getOriginNode(),
					message.getSendTime(),
					message.getMessageId()
			);

			this.sentMessages.add(message.getMessageId());
			toSend.setPropagating(true);
			broadcast.broadcastMessage(node, toSend);
		}
	}


}
