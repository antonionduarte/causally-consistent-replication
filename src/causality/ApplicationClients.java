package causality;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
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
public class ApplicationClients implements EDProtocol, CDProtocol {

	private int numberClients;
	private int weightReads;
	private int weightWrites;

	String prefix;

	public ApplicationClients(String prefix) {
		this.prefix = prefix;
		this.numberClients = Configuration.getInt(ConfigurationProperty.NUMBER_CLIENTS.getString());
		this.weightWrites = Configuration.getInt(ConfigurationProperty.WEIGHT_WRITES.getString());
		this.weightReads = Configuration.getInt(ConfigurationProperty.WEIGHT_READS.getString());
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
		// do nothing
	}


	@Override
	public void nextCycle(Node node, int protocolID) {
		// periodically send an Operation to another Protocol in the Local Node.
	}
}
