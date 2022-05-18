package causalityprotocols.c3;

import simulator.protocols.messages.ProtocolMessage;

public class C3Message implements ProtocolMessage {

	private long[] lblDeps;
	private long writeId;

	public C3Message(long[] lblDeps) {
		this.lblDeps = lblDeps;
	}

	@Override
	public int getSize() {
		return lblDeps.length;
	}

	public long[] getLblDeps() {
		return lblDeps;
	}

	public long getWriteId() {
		return writeId;
	}

	public void setLblDeps(long[] lblDeps) {
		this.lblDeps = lblDeps;
	}
}
