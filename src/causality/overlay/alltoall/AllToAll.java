package causality.overlay.alltoall;

import peersim.config.Configuration;
import peersim.core.Linkable;
import peersim.core.Node;
import peersim.core.Protocol;

import java.util.LinkedList;
import java.util.List;

/**
 * Very simple Overlay where all Nodes are everyone's neighbours.
 */
public class AllToAll implements Protocol, Linkable {

	private List<Node> neighbors;

	public static int allToAllPid;

	public AllToAll(String prefix) {
		allToAllPid = Configuration.getPid(prefix);
	}

	@Override
	public Object clone() {
		try {
			AllToAll clone = (AllToAll) super.clone();
			clone.neighbors = new LinkedList<Node>();
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
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
		if (neighbors.contains(neighbour)) return false;
		this.neighbors.add(neighbour);
		return true;
	}

	@Override
	public boolean contains(Node neighbor) {
		return this.neighbors.contains(neighbor);
	}

	@Override
	public void pack() {}

}
