package simulator.protocols.broadcast.click;

import peersim.config.Configuration;
import peersim.core.CommonState;
import simulator.protocols.broadcast.BroadcastProtocol;
import simulator.protocols.messages.Message;
import peersim.config.FastConfig;
import peersim.core.Node;
import peersim.transport.Transport;

import java.util.List;

public class ClickBroadcast extends BroadcastProtocol {

	private static final String PAR_TRANSPORT = "transport";

	private final int transportId;

	public ClickBroadcast(String prefix) {
		super(prefix);
		transportId = Configuration.getPid(prefix + "." + PAR_TRANSPORT);
	}

	@Override
	public Object clone() {
		return super.clone();
	}

	@Override
	public void uponBroadcast(Node node, Message message, List<Node> neighbors, long lastHop) {
		if (message.getOriginNode().getID() == node.getID()) {
			int pid = BroadcastProtocol.pid;

			for (Node neighbour : neighbors) {
				((Transport) node.getProtocol(transportId)).send(node, neighbour, message, pid);
			}
		}
	}
}
