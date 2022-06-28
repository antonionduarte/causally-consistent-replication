package simulator.protocols.causality;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import simulator.protocols.PendingEvents;
import simulator.protocols.broadcast.Broadcast;
import simulator.protocols.broadcast.BroadcastProtocol;
import simulator.protocols.messages.Message;
import simulator.protocols.messages.MessageWrapper;

import java.util.*;

public abstract class CausalityProtocol implements Causality {

	private static final String PAR_MIGRATION_TIME = "migration_time";
	private static final String PAR_WRITE_TIME = "write_time";
	private static final String PAR_READ_TIME = "read_time";
	private static final String PAR_CHECK_ALL = "check_all";

	private final int writeTime;
	private final int readTime;
	private final int migrationTime;
	private final boolean checkAll;

	/**
	 * Operation Queue, saves the events that weren't able to be processed due to issues with causality.
	 */
	private Queue<Message> pendingOperations;

	public static int pid;

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

		// TODO: These are DEBUG logs.
		if (CommonState.getTime() % 1000 == 0) {
			if (node.getID() == 0) {
				System.out.println("Received Event - Time: " + CommonState.getTime() + " - " +
						message.getMessageId() + " - Node: " + CommonState.getNode().getID());
			}
		}

		switch (message.getEventType()) {
			case PROPAGATING -> {
				if (checkCausality(node, message)) {
					this.executeOperation(node, message);

					// TODO: if message op type is migration, do migration things (maybe not here)
				} else {
					this.pendingOperations.add(message);
				}
			}
			case EXECUTING -> {
				this.visibilityTimes.put(message.getMessageId(), CommonState.getTime());
				this.operationFinishedExecution(node, message);

				if (message.getOriginNode().getID() == node.getID()) {
					((Message) event).setEventType(Message.EventType.RESPONSE);
					EDSimulator.add(0, event, node, PendingEvents.pid);
				}
			}
		}

		if (!sentMessages.contains(message.getMessageId()))
			this.propagateMessage(node, message);
		this.processQueue(node, pid);
	}

	@Override
	public void processQueue(Node node, int pid) {
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

	@Override
	public void executeOperation(Node node, Message message) {
		long expectedArrivalTime = -1;
		this.operationStartedExecution(node, message);

		switch (message.getOperationType()) {
			case READ -> {
				expectedArrivalTime = readTime;
			}
			case WRITE -> {
				expectedArrivalTime = writeTime;
			}
			case MIGRATION -> {
				expectedArrivalTime = migrationTime;
			}
		}

		// TODO: if message op type is migration, do migration things

		Message toSend = new MessageWrapper(message, Message.EventType.EXECUTING, node);
		EDSimulator.add(expectedArrivalTime, toSend, node, PendingEvents.pid);
	}

	public void propagateMessage(Node node, Message message) {
		if (message.getOperationType() == Message.OperationType.WRITE) {
			var lastHop = message.getLastHop();
			var broadcast = (Broadcast) node.getProtocol(BroadcastProtocol.pid);

			Message toSend = new MessageWrapper(message, Message.EventType.PROPAGATING, node);
			this.sentMessages.add(message.getMessageId());
			broadcast.broadcastMessage(node, toSend, lastHop);
		}
	}

	/**
	 * @return The time at which a message was made visible within this node.
	 */
	public Map<String, Long> getVisibilityTimes() {
		return visibilityTimes;
	}

	@Override
	public abstract boolean checkCausality(Node node, Message message);

	@Override
	public abstract void operationFinishedExecution(Node node, Message message);

	@Override
	public abstract void operationStartedExecution(Node node, Message message);
}
