package causalityprotocols.c3;

import simulator.protocols.messages.ProtocolMessage;

import java.util.Map;

public class C3Message implements ProtocolMessage {

	private Map<Long, Long> lblDeps;
	private final long lblId;

	public C3Message(Map<Long, Long> lblDeps, long lblId) {
		this.lblDeps = lblDeps;
		this.lblId = lblId;
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

	public void setLblDeps(Map<Long, Long> lblDeps) {
		this.lblDeps = lblDeps;
	}
}
