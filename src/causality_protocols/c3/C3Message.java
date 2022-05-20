package causality_protocols.c3;

import simulator.protocols.messages.ProtocolMessage;

import java.util.HashMap;
import java.util.Map;

public class C3Message implements ProtocolMessage {

	private Map<Long, Long> lblDeps;
	private long writeId;

	public C3Message(long[] lblDeps) {
		this.lblDeps = new HashMap<>();
	}

	@Override
	public int getSize() {
		return lblDeps.size();
	}

	public Map<Long, Long> getLblDeps() {
		return lblDeps;
	}

	public long getWriteId() {
		return writeId;
	}

	public void setLblDeps(Map<Long, Long> lblDeps) {
		this.lblDeps = lblDeps;
	}
}
