package causality;

import causality.messages.Message;
import causality.messages.ProtocolMessage;

import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;

import java.util.LinkedList;
import java.util.Queue;

public abstract class CausalityProtocolAbstract implements CausalityProtocol {

	/**
	 * The execution time for an operation.
	 */
	private final int writeTime;
	private final int readTime;

	/**
	 * Event Queue, saves the events that weren't able to be processed
	 * due to issues with causality.
	 */
	private final Queue<Message> messageQueue;

	/**
	 * The constructor for the protocol.
	 */
	public CausalityProtocolAbstract() {
		this.messageQueue = new LinkedList<>();
		this.writeTime = Configuration.getInt("WRITE_TIME");
		this.readTime = Configuration.getInt("READ_TIME");
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
		ProtocolMessage protocolMessage = message.getProtocolMessage();

		if (protocolMessage == null) {

		}

		// TODO: This will throw NPE
		if (verifyCausality(message)) { // TODO: Is the order of these if's correct?
			if (message.isPropagating()) {
				message.togglePropagating();
				executeOperation(node, message, pid);
			} else {
				// e.g.: If it's a vector clock, this function should handle incrementing the clock.
				// within the protocol.
				uponMessageExecuted(message);
				// process the eventQueue and check if the events are now valid
				// in respect with causality, and put the available ones in execution.
				processQueue(node, pid);
			}
		} else {
			// if the operation isn't possible due to causality issues add it to the queue.
			messageQueue.add(message);
		}
	}

	@Override
	public void processQueue(Node node, int pid) {
		for (Message message : messageQueue) {
			ProtocolMessage protocolMessage = message.getProtocolMessage();
			if (verifyCausality(message)) {
				messageQueue.remove(message);
				executeOperation(node, message, pid);
			}
		}
	}

	@Override
	public void executeOperation(Node node, Message message, int pid) {
		// Sends message to self with the operation.
		long expectedArrivalTime = CommonState.getTime();

		uponMessageExecuting(message);
		switch (message.getMessageType()) {
			case READ -> expectedArrivalTime += readTime;
			case WRITE ->  expectedArrivalTime += writeTime;
		}

		EDSimulator.add(expectedArrivalTime, message, node, pid);
	}

	private void sendOperation(Node node, Node dest, ProtocolMessage protocolMessage, int pid) {
		((Transport) node.getProtocol(FastConfig.getTransport(pid)))
				.send(node, dest, protocolMessage, pid);
	}

	@Override
	public abstract boolean verifyCausality(Message message);

	@Override
	public abstract void uponMessageExecuted(Message message);

	@Override
	public abstract void uponMessageExecuting(Message message);


}
