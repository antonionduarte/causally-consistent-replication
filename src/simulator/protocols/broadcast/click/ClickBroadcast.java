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
			/*System.out.print(
					"DEBUG: Propagating - " + message.getMessageId()
					+ " - Time:" + CommonState.getNode().getID() + " - Node:" + CommonState.getNode().getID() + " - "
			);*/

			int pid = Configuration.lookupPid(BroadcastProtocol.protName);

			//System.out.print("[");
			for (Node neighbour : neighbors) {
				//System.out.print(neighbour.getID() + ",");
				((Transport) node.getProtocol(transportId))
						.send(node, neighbour, message, pid);
			}
			//System.out.print("]");

			//System.out.println();
			//System.out.println();
		}
	}
}
