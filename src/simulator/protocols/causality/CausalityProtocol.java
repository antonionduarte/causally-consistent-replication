package simulator.protocols.causality;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;
import simulator.node.PartitionsNode;
import simulator.protocols.PendingEvents;
import simulator.protocols.application.ApplicationProtocol;
import simulator.protocols.broadcast.Broadcast;
import simulator.protocols.broadcast.BroadcastProtocol;
import simulator.protocols.messages.Message;
import simulator.protocols.messages.MessageWrapper;
import simulator.protocols.overlay.OverlayProtocol;

import java.util.*;

public abstract class CausalityProtocol implements Causality {

	private static final String PAR_MIGRATION_TIME = "migration_time";
	private static final String PAR_WRITE_TIME = "write_time";
	private static final String PAR_READ_TIME = "read_time";
	private static final String PAR_CHECK_ALL = "check_all";
	private static final String PAR_TRANSPORT = "transport";

	public static int pid;
	public static int transportId;

	private final int writeTime;
	private final int readTime;
	private final int migrationTime;
	private final boolean checkAll;

	/**
	 * Operation Queue, saves the events that weren't able to be processed due to issues with causality.
	 */
	private Queue<Message> pendingOperations;
	/**
	 * Statistic collection variables.
	 */

	private Map<String, Long> visibilityTimes;
	private Set<String> sentMessages;

	/**
	 * The constructor for the protocol.
	 */
	public CausalityProtocol(String prefix) {
		var protName = (prefix.split("\\."))[1];
		pid = Configuration.lookupPid(protName);
		transportId = Configuration.getPid(prefix + "." + PAR_TRANSPORT);
		this.checkAll = Configuration.getBoolean(prefix + "." + PAR_CHECK_ALL);
		this.writeTime = Configuration.getInt(prefix + "." + PAR_WRITE_TIME);
		this.migrationTime = Configuration.getInt(prefix + "." + PAR_MIGRATION_TIME);
		this.readTime = Configuration.getInt(prefix + "." + PAR_READ_TIME);
	}

	@Override
	public Object clone() {
		try {
			CausalityProtocol clone = (CausalityProtocol) super.clone();
			clone.pendingOperations = new LinkedList<>();
			clone.visibilityTimes = new HashMap<>();
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

		if (CommonState.getTime() % 1000 == 0) {
			if (node.getID() == 0) {
				System.out.println("Received Event - Time: " + CommonState.getTime() + " - " +
						message.getMessageId() + " - Node: " + CommonState.getNode().getID());
			}
		}

		this.handleMessage(node, event);

		if (!sentMessages.contains(message.getMessageId())) {
			if (message.getOperationType() == Message.OperationType.MIGRATION) {
				if (!(message.getMigrationTarget() == node.getID())) {
					this.propagateMigration(node, message);
				}
			} else {
				this.propagateMessage(node, message);
			}
		}

		this.processQueue(node);
	}

	/**
	 * @return The time at which a message was made visible within this node.
	 */
	public Map<String, Long> getVisibilityTimes() {
		return visibilityTimes;
	}

	/**
	 * Processes the message, calling the right method according to the type of Event and Operation
	 * of the message.
	 *
	 * @param node The local node.
	 * @param event The message.
	 */
	private void handleMessage(Node node, Object event) {
		var message = (Message) event;
		switch (message.getEventType()) {
			case PROPAGATING -> {
				if (checkCausality(node, message)) {
					this.executeOperation(node, message);
				} else {
					this.pendingOperations.add(message);
				}
			}
			case EXECUTING -> {
				this.visibilityTimes.put(message.getMessageId(), CommonState.getTime());
				if (!(message.getOperationType() == Message.OperationType.MIGRATION)) {
					this.operationFinishedExecution(node, message);
				}
				if (message.getOriginNode().getID() == node.getID()) {
					((Message) event).setEventType(Message.EventType.RESPONSE);
					EDSimulator.add(0, event, node, PendingEvents.pid);
				}
				if (message.getOperationType() == Message.OperationType.MIGRATION) {
					if (message.getMigrationTarget() == node.getID()) {
						System.out.println("AAAAA DOES THIS EVER HAPPEN?!?!?");
						var toSend = new MessageWrapper(message, Message.EventType.RESPONSE, node);
						EDSimulator.add(0, toSend, node, ApplicationProtocol.pid);
					}
				}
			}
		}
	}

	/**
	 * Processes the Message Queue, verifying if there are new operations that
	 * comply to the causality checks of the protocol and are therefore allowed to execute.
	 *
	 * @param node The local node.
	 */
	private void processQueue(Node node) {
		var iterator = pendingOperations.iterator();
		while (iterator.hasNext()) {
			var message = iterator.next();
			if (this.checkCausality(node, message)) {
				iterator.remove();
				this.executeOperation(node, message);
			}
			if (!checkAll) break;
		}
	}

	/**
	 * Decides the amount of time an operation takes to execute, according to it's type.
	 * Processes the operation and calls the internal Protocol methods that change the state
	 * of the protocol upon the execution of an operation.
	 *
	 * @param node The local node.
	 * @param message The message with the operation to execute.
	 */
	private void executeOperation(Node node, Message message) {
		var expectedArrivalTime = -1L;
		var partitionsNode = (PartitionsNode) node;
		if (!(message.getOperationType() == Message.OperationType.MIGRATION)) {
			this.operationStartedExecution(node, message);
		}
		if (partitionsNode.getPartitions().contains(message.getPartition())) {
			switch (message.getOperationType()) {
				case READ -> expectedArrivalTime = readTime;
				case WRITE -> expectedArrivalTime = writeTime;
				case MIGRATION -> expectedArrivalTime = migrationTime;
			}
		} else {
			expectedArrivalTime = 0L;
		}
		var toSend = new MessageWrapper(message, Message.EventType.EXECUTING, node);
		EDSimulator.add(expectedArrivalTime, toSend, node, PendingEvents.pid);
	}

	/**
	 * Propagates a message using the Broadcast Protocol.
	 *
	 * @param node The local node.
	 * @param message The message to propagate.
	 */
	private void propagateMessage(Node node, Message message) {
		if (message.getOperationType() == Message.OperationType.WRITE) {
			var lastHop = message.getLastHop();
			var broadcast = (Broadcast) node.getProtocol(BroadcastProtocol.pid);
			var toSend = new MessageWrapper(message, Message.EventType.PROPAGATING, node);
			this.sentMessages.add(message.getMessageId());
			broadcast.broadcastMessage(node, toSend, lastHop);
		}
	}

	/**
	 * Propagates the Migration message to the necessary nodes.
	 *
	 * @param node The local node.
	 * @param message The migration message to propagate.
	 */
	private void propagateMigration(Node node, Message message) {
		var neighbors = ((OverlayProtocol) node.getProtocol(OverlayProtocol.pid)).getNeighbors();
		var hadTarget = false;
		for (var neighbour : neighbors) {
			if (neighbour.getID() == message.getMigrationTarget()) {
				var toSend = new MessageWrapper(message, Message.EventType.PROPAGATING, node);
				((Transport) node.getProtocol(transportId)).send(node, neighbour, toSend, pid);
				hadTarget = true;
			}
		}
		if (!hadTarget) {
			this.propagateMessage(node, message);
		}
	}

	@Override
	public abstract boolean checkCausality(Node node, Message message);

	@Override
	public abstract void operationFinishedExecution(Node node, Message message);

	@Override
	public abstract void operationStartedExecution(Node node, Message message);
}
