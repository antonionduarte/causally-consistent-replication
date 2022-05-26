package simulator.protocols.overlay.alltoall;

import peersim.core.CommonState;
import simulator.protocols.overlay.OverlayProtocol;
import peersim.core.Linkable;
import peersim.core.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Very simple Overlay where all Nodes are everyone's neighbours.
 */
public class AllToAll extends OverlayProtocol implements Linkable {

	private List<Node> neighbors;

	public static String protName;

	public AllToAll(String prefix) {
		super(prefix);
		protName = (prefix.split("\\."))[1];
	}

	@Override
	public Object clone() {
		AllToAll clone = (AllToAll) super.clone();
		clone.neighbors = new ArrayList<>();
		return clone;
	}

	@Override
	public void onKill() {
		this.neighbors = null;
	}

	@Override
	public int degree() {
		return this.neighbors.size();
	}

	@Override
	public Node getNeighbor(int i) {
		return this.neighbors.get(i);
	}

	@Override
	public boolean addNeighbor(Node neighbour) {
		if (!this.neighbors.contains(neighbour)) {
			this.neighbors.add(neighbour);
			return true;
		}
		return false;
	}

	@Override
	public boolean contains(Node neighbor) {
		for (Node node : neighbors) {
			if (node.getID() == neighbor.getID()) return true;
		}
		return false;
	}

	@Override
	public void pack() {}

	@Override
	public List<Node> getNeighbors() {
		return neighbors;
	}
}
