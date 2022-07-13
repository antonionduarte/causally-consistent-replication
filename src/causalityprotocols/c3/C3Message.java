package causalityprotocols.c3;

import peersim.core.Node;
import simulator.protocols.messages.ProtocolMessage;

import java.util.Map;

public class C3Message implements ProtocolMessage {

	private Map<Long, Long> lblDeps;
	private final long lblId;
	private final Node originNode;

	public C3Message(Map<Long, Long> lblDeps, long lblId, Node originNode) {
		this.lblDeps = lblDeps;
		this.lblId = lblId;
		this.originNode = originNode;
	}

	@Override
	public int getSize() {
		return lblDeps.size();
	}

	public Map<Long, Long> getLblDeps() {
		return lblDeps;
	}

	public long getLblId() {
		return lblId;
	}

	public Node getOriginNode() {
		return this.originNode;
	}

	public void setLblDeps(Map<Long, Long> lblDeps) {
		this.lblDeps = lblDeps;
	}
}
