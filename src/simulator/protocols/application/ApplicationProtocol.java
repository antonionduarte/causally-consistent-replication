package simulator.protocols.application;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;
import simulator.protocols.PendingEvents;
import simulator.protocols.messages.Message;
import simulator.protocols.messages.MessageWrapper;

import java.util.LinkedList;
import java.util.List;

/**
 * Responsible for simulating Clients in the system.
 */
public abstract class ApplicationProtocol implements EDProtocol {

	private final int numberClients;
	private final int weightReads;
	private final int weightWrites;

	private long idCounter;

	private List<String> receivedMessages; // TODO: Not necessary probably
	// TODO: THE STRATEGY HAS SHIFTED, NOW:
	/**
	 * @link talk with fouto
	 */

	private static final String PAR_NUMBER_CLIENTS = "number_clients";
	private static final String PAR_WEIGHT_WRITES = "weight_writes";
	private static final String PAR_WEIGHT_READS = "weight_reads";

	public static int pid;

	// Statistic Collection - Probably will be queried in a control that runs periodically
	private List<Long> messageLatencies;
	private long executedOperations;

	public ApplicationProtocol(String prefix) {
		var protName = (prefix.split("\\."))[1];
		pid = Configuration.lookupPid(protName);
		this.numberClients = Configuration.getInt(prefix + "." + PAR_NUMBER_CLIENTS);
		this.weightWrites = Configuration.getInt(prefix + "." + PAR_WEIGHT_WRITES);
		this.weightReads = Configuration.getInt(prefix + "." + PAR_WEIGHT_READS);
	}

	@Override
	public Object clone() {
		try {
			ApplicationProtocol clone = (ApplicationProtocol) super.clone();
			clone.messageLatencies = new LinkedList<>();
			clone.idCounter = 0;
			clone.executedOperations = 0;
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Sends the initial client side messages.
	 *
	 * @param node The local node.
	 */
	public void startClients(Node node) {
		for (int i = 0; i < numberClients; i++) {
			Message message = getRandomMessage(node);
			this.changeInitialMessage(node, message);
			EDSimulator.add(0, message, node, PendingEvents.pid);
		}
	}

	/**
	 * This function will only trigger when it receives a message back from the Protocol. Basically calculated and
	 * stores statistics, and triggers sending a new message.
	 * It only processes messages that haven't been received yet, the others are discarded.
	 * I'm doing this since it can receive several responses from Nodes that have the correct partition, but only the first
	 * response is relevant.
	 *
	 * @param node the local node
	 * @param pid the identifier of this protocol
	 * @param event the delivered event
	 */
	@Override
	public void processEvent(Node node, int pid, Object event) {
		Message message = (Message) event;

		if (!receivedMessages.contains(message.getMessageId())) {
			// Statistic collection
			long rtt = (CommonState.getTime() - message.getSendTime());
			this.messageLatencies.add(rtt);
			this.receivedMessages.add(message.getMessageId());
			this.executedOperations++;

			// Sends back a new message
			Message toSend = getRandomMessage(node);
			this.changeResponseMessage(node, toSend);
			EDSimulator.add(0, toSend, node, PendingEvents.pid);
		}
	}

	/**
	 * Returns a random message, ready to send to the causality layer, that is either a Write or Read.
	 *
	 * @param node The local node.
	 * @return The message.
	 */
	private Message getRandomMessage(Node node) {
		long totalWeight = weightWrites + weightReads;
		long random = CommonState.random.nextLong(totalWeight);
		Message.OperationType operationType;

		String messageId = node.getID() + "_" + idCounter++;

		// TODO: Determine that an operation is of migration type
		if (random <= weightWrites) {
			operationType = Message.OperationType.WRITE;
		} else {
			operationType = Message.OperationType.READ;
		}

		return new MessageWrapper(
				operationType,
				Message.EventType.PROPAGATING,
				null,
				node,
				CommonState.getTime(),
				CommonState.getNode().getID(),
				messageId
		);
	}

	/**
	 * @return The amount of executed operations.
	 */
	public long getExecutedOperations() {
		return executedOperations;
	}

	/**
	 * @return The list of Client perceived latencies for the node.
	 */
	public List<Long> getMessageLatencies() {
		return this.messageLatencies;
	}

	/**
	 * Implement this function in your Application class if you want the
	 * Initial wrapped {@link simulator.protocols.messages.ProtocolMessage} to be different from null.
	 *
	 * @param node The local node.
	 * @param message The protocol specific message.
	 */
	public abstract void changeInitialMessage(Node node, Message message);

	/**
	 * Implement this function in your Application class if you want the wrapped {@link simulator.protocols.messages.ProtocolMessage}
	 * that are sent as responses in the middle of the simulation to be changed.
	 *
	 * @param node The local node.
	 * @param message The protocol specific message.
	 */
	public abstract void changeResponseMessage(Node node, Message message);
}
