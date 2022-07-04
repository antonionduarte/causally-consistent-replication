package simulator.protocols.overlay.alltoall;

import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import simulator.protocols.overlay.OverlayProtocol;
import simulator.protocols.overlay.SimpleOverlay;

/**
 * Initializes the All-to-All overlay, by assigning making each node everyone's neighbour.
 */
public class AllToAllInitializer implements Control {

	public AllToAllInitializer(String prefix) {
	}

	@Override
	public boolean execute() {
		for (int i = 0; i < Network.size(); i++) {
			Node node = Network.get(i);
			SimpleOverlay overlay = (SimpleOverlay) node.getProtocol(OverlayProtocol.pid);

			for (int j = 0; j < Network.size(); j++) {
				if (j != i) overlay.addNeighbor(Network.get(j));
			}
		}
		return false;
	}
}
