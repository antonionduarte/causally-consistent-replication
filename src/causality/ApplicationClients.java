package causality;

import causality.messages.Message;
import causality.messages.MessageWrapper;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDProtocol;

/**
 * Responsible for simulating Clients in the system.
 *
 * [Ideas]:
 * 	- In the beginning, initialize it, every client sends an Operation to the Node.
 * 	- When a client receives an operation, it immediately replies with another one.
 * 	- The operation is either a Write or a Read, configurable percentage of Writes and Reads.
 * 	- Each Replica has a configurable number of Clients (global config. each replica has that number).
 *
 * [Metrics]:
 * 	- Latency per operation (which we then convert to Medium Latency per Replica and Global. - Maybe Writes/Reads separately.
 * 	- Operations/s, per replica and Globally. - Maybe Writes/Reads separately
 *
 * 	[Random]:
 * 	 - CommonState.r.nextInt/Long etc...
 */
public class ApplicationClients implements EDProtocol {

	private final int numberClients;
	private final int weightReads;
	private final int weightWrites;

	/**
	 * The PID of the causality layer protocol that we should interact with.
	 */
	private static final int causalityPid;

	private static final String NUMBER_CLIENTS_CONFIG = "NUMBER_CLIENTS";
	private static final String WEIGHT_WRITES_CONFIG = "WEIGHT_WRITES";
	private static final String WEIGHT_READS_CONFIG = "WEIGHT_READS";

	private String prefix;

	public ApplicationClients(String prefix) {
		this.prefix = prefix;
		this.numberClients = Configuration.getInt(NUMBER_CLIENTS_CONFIG);
		this.weightWrites = Configuration.getInt(WEIGHT_WRITES_CONFIG);
		this.weightReads = Configuration.getInt(WEIGHT_READS_CONFIG);
	}

	@Override
	public Object clone() {
		try {
			Object clone = super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < numberClients; i++) {
			// send a message to the protocol CausalityProtocol
			long totalWeight = weightWrites + weightReads;
			long random = CommonState.r.nextLong() % totalWeight;

			if (random <= weightReads) {
				// protocol should generate read message and send it to itself
				// readMessage
				Message message = new MessageWrapper(Message.MessageType.READ, null);
				// CausalityProtocol.sendWriteSelf(...);
				return null;
			} else {
				// protocol should generate write message and send it to itself
				// writeMessage
				Message message = new MessageWrapper(Message.MessageType.WRITE, null);
				// CausalityProtocol.sendReadSelf(...);
				return 0;
			}
		}

		return null;
	}

	@Override
	public void processEvent(Node node, int pid, Object event) {
		// do nothing
	}
}
