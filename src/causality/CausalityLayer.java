package causality;

import causality.application.Application;
import causality.messages.Message;
import causality.messages.ProtocolMessage;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;

import java.util.*;

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
		ProtocolMessage protocolMessage = message.getProtocolMessage();

		if (protocolMessage == null) {
			// yadda yadda yadda createMessage or sumthin?
			// ig que nunca há problema com as escritas dos clientes?
			// portanto essa escrita passaria sempre no verifyCausality
			// podia talvez fazer: if protocolMessage == null || verifyCausality(message)

			// o problema de fazer a verificação == null, é que depois dentro do protocolo
			// a pessoa teria de fazer a verificação e criar a mensagem, otherwise isto não funcionaria, o que é
			// um comportamento esquisito...
		}

		// TODO: This will throw NPE
		if (message.getProtocolMessage() == null || verifyCausality(message)) {
			if (message.isPropagating()) {
				message.togglePropagating();
				executeOperation(node, message, pid);
				uponMessageExecuting(message);

				// only sends it back to the application if the message came from the application (same node).
				if (message.getOriginNode().getID() == node.getID()) {
					EDSimulator.add(0, event, node, Application.applicationPid);
				}
			} else {
				visibilityTimes.put(message.getMessageId(), CommonState.getTime());

				if (message.getMessageType() == Message.MessageType.WRITE) {
					// TODO: Propagate message to other nodes
				}

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
