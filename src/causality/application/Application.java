package causality.application;

import causality.CausalityLayer;
import causality.messages.Message;
import causality.messages.MessageWrapper;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;

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
public class Application implements EDProtocol {

	private final int numberClients;
	private final int weightReads;
	private final int weightWrites;

	private static final String NUMBER_CLIENTS_CONFIG = "NUMBER_CLIENTS";
	private static final String WEIGHT_WRITES_CONFIG = "WEIGHT_WRITES";
	private static final String WEIGHT_READS_CONFIG = "WEIGHT_READS";

	public static int applicationPid;

	// Statistic Collection - Probably will be queried in a control that runs periodically
	private List<Long> messageLatencies;

	// PID of protocol: prefix-of-protocol and probably getPID from Configuration.
	// this can probably receive messages as well, and send them back
	// probably also wants to keep statistics on OP/s and Latency.
	public Application(String prefix) {
		this.numberClients = Configuration.getInt(NUMBER_CLIENTS_CONFIG);
		this.weightWrites = Configuration.getInt(WEIGHT_WRITES_CONFIG);
		this.weightReads = Configuration.getInt(WEIGHT_READS_CONFIG);

		applicationPid = Configuration.getPid(prefix);
	}

	@Override
	public Object clone() {
		try {
			Application clone = (Application) super.clone();
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
			EDSimulator.add(0, message, node, CausalityLayer.causalityPid);
		}
	}

	/**
	 * This function will only trigger when it receives a message back from the Protocol. Basically calculated and
	 * stores statistics, and triggers sending a new message.
	 *
	 * @param node  the local node
	 * @param pid   the identifier of this protocol
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
		EDSimulator.add(0, toSend, node, CausalityLayer.causalityPid);
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

		if (random <= weightReads) {
			messageType = Message.MessageType.READ;
		} else {
			messageType = Message.MessageType.WRITE;
		}

		return new MessageWrapper(messageType, null, node, CommonState.getTime());
	}
}
