package causalityprotocols.gentlerain;

import peersim.cdsim.CDProtocol;
import peersim.core.Node;
import simulator.protocols.causality.CausalityProtocol;
import simulator.protocols.messages.Message;
import simulator.protocols.overlay.OverlayProtocol;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GentleRain extends CausalityProtocol implements CDProtocol {

	/**
	 * objectId -> version (timestamp)
	 */
	private Map<Long, Long> versionChain;

	/**
	 * A (VV) with the latest updates seen from each replica.
	 */
	private Map<Long, Long> versionVector;

	/**
	 * NodeId -> LST
	 */
	private Map<Long, Long> localStableTime;

	private long globalStableTime; // the GST of all nodes

	// clients send their GST to the server that serves the partition containing the item.
	// the server first updates it's GST if it's smaller than the client's

	/**
	 * The constructor for the protocol.
	 */
	public GentleRain(String prefix) {
		super(prefix);
	}

	@Override
	public Object clone() {
		GentleRain clone = (GentleRain) super.clone();
		clone.versionVector = new HashMap<>();
		clone.localStableTime = new HashMap<>();
		clone.globalStableTime = 0L;
		clone.versionChain = new HashMap<>();
	}

	@Override
	public boolean checkCausality(Node node, Message message) {
		var protocolMessage = (GentleRainMessage) message;

		if (message.getOperationType() == Message.OperationType.MIGRATION) {

		}

		if (message.getOperationType() == Message.OperationType.WRITE) {
			// TODO: All in all, insert the value into the VV
			if (message.getOriginNode().getID() == node.getID()) {
				// do something
			}
			// also write into the correct versionChain value taking into
			// consideration the itemId stored in the message.
		}

		if (message.getOperationType() == Message.OperationType.READ) {
			// do something
		}

		return false;
	}

	@Override
	public void operationFinishedExecution(Node node, Message message) {
		// probably nothing?
	}

	@Override
	public void operationStartedExecution(Node node, Message message) {
		//  probably nothing?
	}

	/**
	 * Updates GST and LST.
	 * @param node       the node on which this component is run
	 * @param protocolID the id of this protocol in the protocol array
	 */
	@Override
	public void nextCycle(Node node, int protocolID) {
		this.globalStableTime = Collections.min(this.localStableTime.values());
		/**
		 * let overlayProt = ...
		 * overlayProt.getNeighbours()
		 * let transport = ...
		 * for n in neighbours:
		 * 		transport.send(n, globalStableTime, ...) # HEARTBEAT + GST?
 		 */
	}
}
