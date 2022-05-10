package causality;

import causality.messages.Message;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;

import java.util.ArrayDeque;
import java.util.Queue;

public abstract class CausalityProtocolAbstract implements CausalityProtocol {

	public static final int DEFAULT_EXEC_TIME = 10;
	public static final String EXEC_TIME_PROPERTY = "EXECUTION_TIME";

	/**
	 * The execution time for an operation.
	 */
	private final int execTime;

	/**
	 * Event Queue, saves the events that weren't able to be processed
	 * due to issues with causality.
	 */
	private final Queue<Message> messageQueue;

	/**
	 * The constructor for the protocol.
	 * TODO: Clone - Make this work or something
	 */
	public CausalityProtocolAbstract() {
		this.messageQueue = new ArrayDeque<>();
		this.execTime = Configuration.getInt(EXEC_TIME_PROPERTY);
	}

	@Override
	public Object clone() {
		try {
			Object clone = super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void processEvent(Node node, int pid, Object event) {
		// process the incoming event
		Message message = (Message) event;

		if (verifyCausality(message)) {
			if (message.getMessageType() == Message.MessageType.PROPAGATING) {
				message.setMessageType(Message.MessageType.EXECUTING);
				executeOperation(node, message, pid);
			} else {
				// e.g.: If it's a vector clock, this function should handle incrementing the clock.
				// within the protocol.
				processProtocolMessage(message);
				// process the eventQueue and check if the events are now valid
				// in respect with causality, and put the available ones in execution.
				processQueue(node, pid);
			}
		} else {
			// if the operation isn't possible due to causality issues
			// add it to the queue.
			messageQueue.add(message);
		}
	}

	@Override
	public void processQueue(Node node, int pid) {
		for (Message message : messageQueue) {
			if (verifyCausality(message)) {
				messageQueue.remove(message);
				executeOperation(node, message, pid);
			}
		}
	}

	@Override
	public void executeOperation(Node node, Message message, int pid) {
		// Sends message to self with the operation.
		message.setExecutionTime(execTime);
		EDSimulator.add(execTime, message, node, pid);
	}

	private void sendOperation(Node node, Node dest, Message message, int pid) {
		((Transport) node.getProtocol(FastConfig.getTransport(pid)))
				.send(node, dest, message, pid);
	}

	@Override
	public abstract boolean verifyCausality(Message message);

	@Override
	public abstract void processProtocolMessage(Message message);

	@Override
	public abstract void messageExecutingProtocol(Message message);


}
