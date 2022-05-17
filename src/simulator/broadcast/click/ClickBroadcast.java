package simulator.broadcast.click;

import simulator.broadcast.BroadcastProtocol;
import simulator.messages.Message;
import peersim.config.FastConfig;
import peersim.core.Node;
import peersim.transport.Transport;

import java.util.List;

public class ClickBroadcast extends BroadcastProtocol {

	public ClickBroadcast(String prefix) {
		super(prefix);
	}

	@Override
	public Object clone() {
		return super.clone();
	}

	@Override
	public void uponBroadcast(Node node, Message message, List<Node> neighbors) {
		for (Node neighbour : neighbors) {
			((Transport) node.getProtocol(FastConfig.getTransport(BroadcastProtocol.broadcastPid)))
					.send(node, neighbour, message, BroadcastProtocol.broadcastPid);
		}
	}
}
