package causalityprotocols.saturn;

import peersim.core.CommonState;
import peersim.core.Node;
import simulator.protocols.broadcast.BroadcastProtocol;
import simulator.protocols.messages.Message;

import java.util.List;

public class SaturnBroadcast extends BroadcastProtocol {

	public SaturnBroadcast(String prefix) {
		super(prefix);
	}

	@Override
	public void uponBroadcast(Node node, Message message, List<Node> neighbors) {
		var saturnMessage = (SaturnMessage) message.getProtocolMessage();
		var receivedFromId = saturnMessage.getReceivedFromId();

		saturnMessage.setReceivedFromId(CommonState.getNode().getID());

		for (var neighbor : neighbors) {
			if (neighbor.getID() != receivedFromId) {
				((Transport) node.getProtocol(transportId))
					.send(node, neighbour, message, pid);
			}
		}
	}

}
