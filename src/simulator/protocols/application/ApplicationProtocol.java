package simulator.protocols.application;

import simulator.protocols.CausalityProtocol;
import simulator.protocols.messages.Message;
import simulator.protocols.messages.MessageWrapper;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;
import simulator.protocols.messages.ProtocolMessage;

import java.util.LinkedList;
import java.util.List;

/**
 * Responsible for simulating Clients in the system.
 * <p>
 * [Ideas]: - In the beginning, initialize it, every client sends an Operation to the Node. - When a client receives an
 * operation, it immediately replies with another one. - The operation is either a Write or a Read, configurable
 * percentage of Writes and Reads. - Each Replica has a configurable number of Clients (global config. each replica has
 * that number).
 * <p>
 * [Metrics]: - Latency per operation (which we then convert to Medium Latency per Replica and Global. - Maybe
 * Writes/Reads separately. - Operations/s, per replica and Globally. - Maybe Writes/Reads separately
 * <p>
 * [Random]: - CommonState.r.nextInt/Long etc...
 */
public abstract class ApplicationProtocol implements EDProtocol {

	private final int numberClients;
	private final int weightReads;
	private final int weightWrites;

	private long idCounter;

	private static final String NUMBER_CLIENTS_CONFIG = "number_clients";
	private static final String WEIGHT_WRITES_CONFIG = "weight_writes";
	private static final String WEIGHT_READS_CONFIG = "weight_reads";

	public static int applicationPid;

	// Statistic Collection - Probably will be queried in a control that runs periodically
	private List<Long> messageLatencies;

	public ApplicationProtocol(String prefix) {
		this.numberClients = Configuration.getInt(prefix + "." + NUMBER_CLIENTS_CONFIG);
		this.weightWrites = Configuration.getInt(prefix + "." + WEIGHT_WRITES_CONFIG);
		this.weightReads = Configuration.getInt(prefix + "." + WEIGHT_READS_CONFIG);

		applicationPid = Configuration.getPid(prefix);
	}

	@Override
	public Object clone() {
		try {
			ApplicationProtocol clone = (ApplicationProtocol) super.clone();
			clone.messageLatencies = new LinkedList<>();
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
			this.changeInitialMessage(node, message.getProtocolMessage());
			EDSimulator.add(0, message, node, CausalityProtocol.causalityPid);
		}
	}

	/**
	 * This function will only trigger when it receives a message back from the Protocol. Basically calculated and
	 * stores statistics, and triggers sending a new message.
	 *
	 * @param node the local node
	 * @param pid the identifier of this protocol
	 * @param event the delivered event
	 */
	@Override
	public void processEvent(Node node, int pid, Object event) {
		// Statistic collection
		Message message = (Message) event;
		long rtt = (CommonState.getTime() - message.getSendTime());
		messageLatencies.add(rtt / 2);

		// Sends back a new message
		Message toSend = getRandomMessage(node);
		this.changeResponseMessage(node, toSend.getProtocolMessage());
		EDSimulator.add(0, toSend, node, CausalityProtocol.causalityPid);
	}

	/**
	 * Returns a random message, ready to send to the causality layer, that is either a Write or Read.
	 *
	 * @param node The local node.
	 * @return The message.
	 */
	private Message getRandomMessage(Node node) {
		long totalWeight = weightWrites + weightReads;
		long random = CommonState.random.nextLong() % totalWeight;
		Message.MessageType messageType;

		String messageId = node.getID() + "_" + idCounter++;

		if (random <= weightReads) {
			messageType = Message.MessageType.READ;
		} else {
			messageType = Message.MessageType.WRITE;
		}

		return new MessageWrapper(messageType, null, node, CommonState.getTime(), messageId);
	}

	/**
	 * @return The list of Client perceived latencies for the node.
	 */
	public List<Long> getMessageLatencies() {
		return messageLatencies;
	}

	/**
	 * Implement this function in your Application class if you want the
	 * Initial wrapped {@link simulator.protocols.messages.ProtocolMessage} to be different than null.
	 *
	 * @param node The local node.
	 * @param message The protocol specific message.
	 */
	public abstract void changeInitialMessage(Node node, ProtocolMessage message);

	/**
	 * Implement this function in your Application class if you want the wrapped {@link simulator.protocols.messages.ProtocolMessage}
	 * that are sent as responses in the middle of the simulation to be changed.
	 *
	 * @param node The local node.
	 * @param message The protocol specific message.
	 */
	public abstract void changeResponseMessage(Node node, ProtocolMessage message);
}
