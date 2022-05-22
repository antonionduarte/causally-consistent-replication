package simulator.protocols.overlay.alltoall;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import simulator.protocols.overlay.Overlay;
import simulator.protocols.overlay.OverlayProtocol;

/**
 * Initializes the All-to-All overlay, by assigning making each node everyone's neighbour.
 */
public class AllToAllInitializer implements Control {

	public AllToAllInitializer(String prefix) { }

	@Override
	public boolean execute() {
		for (int i = 0; i < Network.size(); i++) {
			Node node = Network.get(i);
			AllToAll overlay = (AllToAll) node.getProtocol(Configuration.getPid(OverlayProtocol.overlayPrefix));

			for (int j = 0; i < Network.size(); i++) {
				if (j != i) overlay.addNeighbor(Network.get(j));
			}
		}
		return false;
	}
}
