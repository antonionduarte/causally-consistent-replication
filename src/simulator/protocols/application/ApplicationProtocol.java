package simulator.protocols.application;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;
import simulator.node.PartitionsNode;
import simulator.protocols.PendingEvents;
import simulator.protocols.messages.Message;
import simulator.protocols.messages.MessageBuilder;
import simulator.protocols.messages.ProtocolMessage;

import java.util.LinkedList;
import java.util.List;

/**
 * Responsible for simulating Clients in the system.
 */
public abstract class ApplicationProtocol implements EDProtocol {

	private static final String PAR_NUMBER_CLIENTS = "number_clients";
	private static final String PAR_WEIGHT_WRITES = "weight_writes";
	private static final String PAR_WEIGHT_READS = "weight_reads";

	public static int pid;
	private final int numberClients;
	private final int weightReads;
	private final int weightWrites;
	private long idCounter;
	private long executedOperations;
	private List<String> receivedMessages;
	private List<Long> messageLatencies;

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
			clone.receivedMessages = new LinkedList<>();
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
			var message = getRandomMessage(node);
			this.changeInitialMessage(node, message);
			EDSimulator.add(0, message, node, PendingEvents.pid);
			System.out.println("(AL - Init) - Node: " + node.getID() + " - Time: " + CommonState.getTime() + " - MigT: " + message.getMigrationTarget() + " - Id: " + message.getMessageId());
		}
	}

	/**
	 * This function will only trigger when it receives a message back from the Protocol. Basically calculated and
	 * stores statistics, and triggers sending a new message. It only processes messages that haven't been received yet,
	 * the others are discarded. I'm doing this since it can receive several responses from Nodes that have the correct
	 * partition, but only the first response is relevant.
	 *
	 * @param node the local node
	 * @param pid the identifier of this protocol
	 * @param event the delivered event
	 */
	@Override
	public void processEvent(Node node, int pid, Object event) {
		var message = (Message) event;

		if (!receivedMessages.contains(message.getMessageId())) {
			this.uponReceiveMessage(node, message);

			if (message.getOperationType() == Message.OperationType.MIGRATION) {
				var toSend = getRandomPartitionMessage(node, message, message.getPartition());

				System.out.println("(AL - 1) - Mig. Targ: " + toSend.getMigrationTarget() + " - Node: " + node.getID() + " - Part: " + toSend.getPartition() + " - Time: " + CommonState.getTime() + " - Id: " + message.getMessageId());

				this.changeResponseMessage(node, toSend);
				EDSimulator.add(0, toSend, node, PendingEvents.pid);
			} else {
				var rtt = (CommonState.getTime() - message.getSendTime());
				this.messageLatencies.add(rtt);
				this.receivedMessages.add(message.getMessageId());
				this.executedOperations++;

				var toSend = getRandomMessage(node);

				System.out.println("(AL - 2) - Mig. Targ: " + toSend.getMigrationTarget() + " - Node: " + node.getID() + " - Part: " + toSend.getPartition() + " - Time: " + CommonState.getTime() + " - Id: " + message.getMessageId());

				this.changeResponseMessage(node, toSend);
				EDSimulator.add(0, toSend, node, PendingEvents.pid);
			}
		}
	}

	/**
	 * Returns a random message, ready to send to the causality layer, that is either a Write or Read or Migration.
	 * In case it is a Migration message, it includes a valid migrationTarget (the node to migrate to).
	 *
	 * @param node The local node.
	 * @return The message.
	 */
	private Message getRandomMessage(Node node) {
		var partition = selectPartition(node);
		var partitionNode = (PartitionsNode) node;
		var partitions = partitionNode.getPartitions();
		var totalWeight = weightWrites + weightReads;
		var random = CommonState.random.nextLong(totalWeight);
		var messageBuilder = new MessageBuilder();

		messageBuilder.setMessageId(node.getID() + "_" + idCounter++);

		if (partitions.contains(partition)) {
			if (random <= weightWrites) {
				messageBuilder.setOperationType(Message.OperationType.WRITE);
			} else {
				messageBuilder.setOperationType(Message.OperationType.READ);
			}
		} else {
			messageBuilder.setOperationType(Message.OperationType.MIGRATION);
			messageBuilder.setMigrationTarget(selectMigrateNode(node, partition));
		}

		return messageBuilder
				.setEventType(Message.EventType.PROPAGATING)
				.setOriginNode(node)
				.setPartition(partition)
				.setSendTime(CommonState.getTime())
				.setLastHop(node.getID())
				.build();
	}

	/**
	 * Returns a Message with the given partition and the given protocol message.
	 * Necessary when the protocol receives a Migration message and needs to propagate a new
	 * read or write message with the given partition and already existing protocol message.
	 *
	 * @param node The local node.
	 * @param protocolMessage The protocol message.
	 * @param partition The partition.
	 * @return A Message with the specified properties.
	 */
	private Message getRandomPartitionMessage(Node node, Message message, char partition) {
		var protocolMessage = (ProtocolMessage) message.getProtocolMessage();
		var messageBuilder = new MessageBuilder();

		messageBuilder.setOperationType(Message.OperationType.WRITE);
		messageBuilder.setMessageId(node.getID() + "_" + idCounter++);

		return messageBuilder
				.setEventType(Message.EventType.PROPAGATING)
				.setOriginNode(node)
				.setProtocolMessage(protocolMessage)
				.setPartition(partition)
				.setSendTime(message.getSendTime())
				.setLastHop(node.getID())
				.build();
	}

	/**
	 * Selects a valid node for the current node to migrate to,
	 * i.e a random node in the set of nodes with the required partition.
	 *
	 * @param node The local node.
	 * @param partition The necessary partition.
	 * @return The nodeId of a valid node to migrate to.
	 */
	private long selectMigrateNode(Node node, char partition) {
		var partitionNode = (PartitionsNode) node;
		var partitions = partitionNode.getAllPartitions();
		var possibleNodes = new LinkedList<Long>();
		for (var toCheck : partitions.keySet()) {
			if (partitions.get(toCheck).contains(partition)) {
				possibleNodes.add(toCheck);
			}
		}
		var selectedIndex = CommonState.random.nextLong(possibleNodes.size());
		return possibleNodes.get((int) selectedIndex);
	}

	/**
	 * Selects a random partition from the list of distinct partitions.
	 *
	 * @param node The local node.
	 * @return A char that represents the partition.
	 */
	private char selectPartition(Node node) {
		var partitionNode = (PartitionsNode) node;
		var partitionIndex = CommonState.random.nextLong(partitionNode.getDistinctPartitions().size());
		return partitionNode.getDistinctPartitions().get((int) partitionIndex);
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
	 * Implement this function in Application class if you want the receipt of a message to have
	 * an effect on the Application Layer.
	 *
	 * @param node The local node.
	 * @param message The message.
	 */
	public abstract void uponReceiveMessage(Node node, Message message);

	/**
	 * Implement this function in your Application class if you want the Initial wrapped
	 * {@link simulator.protocols.messages.ProtocolMessage} to be different from null.
	 * Useful if the Protocol requires the Clients / Client layer to save state.
	 *
	 * @param node The local node.
	 * @param message The protocol specific message.
	 */
	public abstract void changeInitialMessage(Node node, Message message);

	/**
	 * Implement this function in your Application class if you want the wrapped
	 * {@link simulator.protocols.messages.ProtocolMessage} that are sent as responses in the middle of the simulation
	 * to be changed.
	 * Useful if the Protocol requires the Clients / Client layer to save state.
	 *
	 * @param node The local node.
	 * @param message The protocol specific message.
	 */
	public abstract void changeResponseMessage(Node node, Message message);
}
