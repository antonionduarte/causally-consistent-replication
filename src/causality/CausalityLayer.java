package causality;

import causality.application.Application;
import causality.broadcast.Broadcast;
import causality.broadcast.BroadcastProtocol;
import causality.messages.Message;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDSimulator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
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
	 * Statistic collection structure - Visibility times.
	 */
	Map<String, Long> visibilityTimes;

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
			clone.visibilityTimes = new HashMap<>();
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

		// TODO: Right now this will throw NPE, but Application will now be an Abstract class as well
		if (verifyCausality(message)) {
			if (message.isPropagating()) {
				message.togglePropagating();
				executeOperation(node, message, pid);
				uponMessageExecuting(message);

				// only sends it back to the application if the message came from the application (same node).
				if (message.getOriginNode().getID() == node.getID()) {
					EDSimulator.add(0, event, node, Application.applicationPid);
				}
			} else {
				this.visibilityTimes.put(message.getMessageId(), CommonState.getTime());

				if (message.getMessageType() == Message.MessageType.WRITE) {
					Broadcast broadcast = (Broadcast) node.getProtocol(BroadcastProtocol.broadcastPid);
					broadcast.broadcastMessage(node, message);
				}

				uponMessageExecuted(message);
				processQueue(node, pid);
			}
		} else {
			this.messageQueue.add(message);
		}
	}

	@Override
	public void processQueue(Node node, int pid) {
		for (Message message : messageQueue) {
			if (verifyCausality(message)) {
				this.messageQueue.remove(message);
				executeOperation(node, message, pid);
			}
		}
	}

	@Override
	public void executeOperation(Node node, Message message, int pid) {
		// Sends message to self with the operation.
		long expectedArrivalTime;
		uponMessageExecuting(message);

		if (message.getMessageType() == Message.MessageType.READ) {
			expectedArrivalTime = readTime;
		} else {
			expectedArrivalTime = writeTime;
		}

		EDSimulator.add(expectedArrivalTime, message, node, pid);
	}

	@Override
	public abstract boolean verifyCausality(Message message);

	@Override
	public abstract void uponMessageExecuted(Message message);

	@Override
	public abstract void uponMessageExecuting(Message message);


}
