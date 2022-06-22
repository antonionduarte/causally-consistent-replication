package simulator.protocols.broadcast;

import simulator.protocols.causality.Causality;
import simulator.protocols.causality.CausalityProtocol;
import simulator.protocols.messages.Message;
import simulator.protocols.overlay.OverlayProtocol;
import peersim.config.Configuration;
import peersim.core.Node;

import java.util.List;

public abstract class BroadcastProtocol implements Broadcast {

	public static String protName;

	public BroadcastProtocol(String prefix) {
		protName = (prefix.split("\\."))[1];
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
		var causalityPid = Configuration.lookupPid(CausalityProtocol.protName);
		var causalityLayer = (Causality) node.getProtocol(causalityPid);
		causalityLayer.processEvent(node, pid, event); // TODO: Probably can't do this -> send to PendingEvents
	}

	@Override
	public void broadcastMessage(Node node, Message message, long lastHop) {
		var causalityPid = Configuration.lookupPid(OverlayProtocol.protName);
		var neighbors = ((OverlayProtocol) node.getProtocol(causalityPid)).getNeighbors();
		uponBroadcast(node, message, neighbors, lastHop);
	}

	/**
	 * Needs to be implemented on a Broadcast protocol and manages how the message
	 * is sent to the list of neighbors of the current node.
	 * @param node The local node.
	 * @param message The message to broadcast.
	 * @param neighbors The list of neighbors.
	 * @param lastHop The last hop that the message went through
	 */
	public abstract void uponBroadcast(Node node, Message message, List<Node> neighbors, long lastHop);
}
