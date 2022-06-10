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

	long numPush;
	long numPop;

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
		System.out.println("RECEIVED EVENT - Time: " + CommonState.getTime() + " - Node: " + CommonState.getNode().getID());

		var message = (Message) event;
		// Could throw NPE if not well verified within the protocol
		if (message.isPropagating()) {
			if (checkCausality(node, message)) {
				System.out.println(
					"DEBUG: Verifies causality - Time:" + CommonState.getTime() + " - " + message.getMessageId() +
					" - Node:" + CommonState.getNode().getID()
				);
				message.togglePropagating();
				this.executeOperation(node, message, pid);
			}
			else {
				System.out.println(
					"DEBUG: Doesn't verify causality - Time:" + CommonState.getTime() + " - " + message.getMessageId() +
					" - Node:" + CommonState.getNode().getID()
				);
				if (!executedMessages.contains(message.getMessageId())) {
					this.operationQueue.add(message);
				}
			}
		}
		// Message was executing
		else {
			this.visibilityTimes.put(message.getMessageId(), CommonState.getTime());
			this.executedOperations++;
			this.operationFinishedExecution(node, message);

			// Send the response back to the client
			if (message.getOriginNode().getID() == node.getID()) {
				EDSimulator.add(0, event, node, Configuration.lookupPid(ApplicationProtocol.protName));
			}
		}

		if (!sentMessages.contains(message.getMessageId())) {
			this.propagateMessage(node, message);
		}

		this.processQueue(node, pid);
	}

	@Override
	public void processQueue(Node node, int pid) {
		var verifiedMessages = new ArrayList<Message>();
		for (Message message : operationQueue) {
			if (checkCausality(node, message)) {
				verifiedMessages.add(message);
				this.executeOperation(node, message, pid);
			}
		}

		this.operationQueue.removeAll(verifiedMessages);
	}

	@Override
	public void executeOperation(Node node, Message message, int pid) {
		long expectedArrivalTime;
		this.executedMessages.add(message.getMessageId());
		this.operationStartedExecution(node, message);

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
	public abstract boolean checkCausality(Node node, Message message);

	@Override
	public abstract void operationFinishedExecution(Node node, Message message);

	@Override
	public abstract void operationStartedExecution(Node node, Message message);

	public void propagateMessage(Node node, Message message) {
		if (message.getMessageType() == Message.MessageType.WRITE) {
			var broadcast = (Broadcast) node.getProtocol(Configuration.lookupPid(BroadcastProtocol.protName));

			Message toSend = new MessageWrapper(
					message.getMessageType(),
					message.getProtocolMessage(),
					message.getOriginNode(),
					message.getSendTime(),
					node.getID(),
					message.getMessageId()
			);

			this.sentMessages.add(message.getMessageId());
			toSend.setPropagating(true);
			broadcast.broadcastMessage(node, toSend);
		}
	}


}
