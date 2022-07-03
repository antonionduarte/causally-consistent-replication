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
		for (var neighbor : neighbors) {
			if (neighbor.getID() != lastHop) {
				((Transport) node.getProtocol(transportId)).send(node, neighbor, message, BroadcastProtocol.pid);
			}
		}
	}
}
