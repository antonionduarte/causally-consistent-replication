package causality;

import causality.application.Application;
import causality.messages.Message;
import causality.messages.ProtocolMessage;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;

import java.util.LinkedList;
import java.util.Queue;

public abstract class CausalityLayer implements Causality {

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
	 * The constructor for the protocol.
	 */
	public CausalityLayer(String prefix) {
		causalityPid = Configuration.getPid(prefix);

		this.writeTime = Configuration.getInt("WRITE_TIME");
		this.readTime = Configuration.getInt("READ_TIME");
	}

	@Override
	public Object clone() {
		try {
			CausalityLayer clone = (CausalityLayer) super.clone();
			clone.messageQueue = new LinkedList<>();
			return clone;
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
		if (verifyCausality(message)) {
			if (message.isPropagating()) {
				message.togglePropagating();
				executeOperation(node, message, pid);
				uponMessageExecuting(message);

				// only sends it back to the application if the message came from the application (same node).
				// TODO: check with someone that knows what they're doing that this makes sense
				if (message.getOriginNode().getID() == node.getID()) {
					EDSimulator.add(0, event, node, Application.applicationPid);
				}
			} else {
				uponMessageExecuted(message);
				processQueue(node, pid);
			}
		} else {
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
		long expectedArrivalTime = 0;

		uponMessageExecuting(message);
		switch (message.getMessageType()) {
			case READ -> expectedArrivalTime = readTime;
			case WRITE -> expectedArrivalTime = writeTime;
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
