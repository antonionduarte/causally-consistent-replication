package simulator.protocols.broadcast;

import peersim.config.Configuration;
import peersim.core.Node;
import simulator.protocols.PendingEvents;
import simulator.protocols.messages.Message;
import simulator.protocols.overlay.OverlayProtocol;

import java.util.List;

public abstract class BroadcastProtocol implements Broadcast {

	public static int pid;

	public BroadcastProtocol(String prefix) {
		var protName = (prefix.split("\\."))[1];
		pid = Configuration.lookupPid(protName);
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
		var pendingEvents = (PendingEvents) node.getProtocol(PendingEvents.pid);
		pendingEvents.processEvent(node, pid, event);
	}

	@Override
	public void broadcastMessage(Node node, Message message, long lastHop) {
		var neighbors = ((OverlayProtocol) node.getProtocol(OverlayProtocol.pid)).getNeighbors();
		this.uponBroadcast(node, message, neighbors, lastHop);
	}

	/**
	 * Needs to be implemented on a Broadcast protocol and manages how the message
	 * is sent to the list of neighbors of the current node.
	 *
	 * @param node The local node.
	 * @param message The message to broadcast.
	 * @param neighbors The list of neighbors.
	 * @param lastHop The last hop that the message went through
	 */
	public abstract void uponBroadcast(Node node, Message message, List<Node> neighbors, long lastHop);
}
