package simulator.protocols.broadcast;

import simulator.protocols.Causality;
import simulator.protocols.CausalityProtocol;
import simulator.protocols.messages.Message;
import simulator.protocols.overlay.OverlayProtocol;
import peersim.config.Configuration;
import peersim.core.Node;

import java.util.List;

public abstract class BroadcastProtocol implements Broadcast {

	public static String broadcastPrefix;

	public BroadcastProtocol(String prefix) {
		broadcastPrefix = prefix;
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void processEvent(Node node, int pid, Object event) {
		Causality causalityLayer = (Causality) node.getProtocol(Configuration.getPid(CausalityProtocol.causalityPrefix));
		causalityLayer.processEvent(node, Configuration.getPid(CausalityProtocol.causalityPrefix), event);
	}

	@Override
	public void broadcastMessage(Node node, Message message) {
		List<Node> neighbors = ((OverlayProtocol) node.getProtocol(Configuration.getPid(OverlayProtocol.overlayPrefix)))
				.getNeighbors();
		uponBroadcast(node, message, neighbors);
	}

	/**
	 * Needs to be implemented on a Broadcast protocol and manages how the message
	 * is sent to the list of neighbors of the current node.
	 * @param node The local node.
	 * @param message The message to broadcast.
	 * @param neighbors The list of neighbors.
	 */
	public abstract void uponBroadcast(Node node, Message message, List<Node> neighbors);
}