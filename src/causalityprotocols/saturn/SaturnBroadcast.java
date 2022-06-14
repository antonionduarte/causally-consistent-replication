package causalityprotocols.saturn;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.transport.Transport;
import simulator.protocols.broadcast.BroadcastProtocol;
import simulator.protocols.messages.Message;

import java.util.List;

public class SaturnBroadcast extends BroadcastProtocol {

	private static final String PAR_TRANSPORT = "transport";

	private final int transportId;

	public SaturnBroadcast(String prefix) {
		super(prefix);
		transportId = Configuration.getPid(prefix + "." + PAR_TRANSPORT);
	}

	@Override
	public Object clone() {
		return super.clone();
	}

	@Override
	public void uponBroadcast(Node node, Message message, List<Node> neighbors, long lastHop) {
		int pid = Configuration.lookupPid(BroadcastProtocol.protName);

		for (var neighbor : neighbors) {
			// send to everyone that is not himself
			if (neighbor.getID() != lastHop) {
				System.out.println(
						"DEBUG: Propagating - " + message.getMessageId()
						+ " - Time:" + CommonState.getTime() + " - Node:" + CommonState.getNode().getID() + " - " +
								neighbor.getID()
				);

				((Transport) node.getProtocol(transportId)).send(node, neighbor, message, pid);
			}
		}
	}

}
