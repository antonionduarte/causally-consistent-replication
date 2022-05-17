package causality.overlay;

import peersim.core.Linkable;
import peersim.core.Node;

import java.util.List;

public interface Overlay extends Linkable {

	/**
	 * @return The list of neighbors of the current node in the overlay.
	 */
	List<Node> getNeighbors();

}
