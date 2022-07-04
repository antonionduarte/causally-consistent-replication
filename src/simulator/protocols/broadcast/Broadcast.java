package simulator.protocols.broadcast;

import peersim.core.Node;
import peersim.edsim.EDProtocol;
import simulator.protocols.messages.Message;

public interface Broadcast extends EDProtocol {

	/**
	 * Implemented in the {@link BroadcastProtocol} abstract class.
	 * It retrieves the list of neighbors of the current node from the overlay protocol
	 * and calls the uponBroadcast function.
	 *
	 * @param node The current node.
	 * @param message The message to broadcast.
	 * @param lastHop The last node the message went by.
	 */
	void broadcastMessage(Node node, Message message, long lastHop);
}
