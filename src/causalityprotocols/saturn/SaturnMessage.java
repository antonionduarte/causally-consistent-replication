package causalityprotocols.saturn;

import simulator.protocols.messages.ProtocolMessage;

public class SaturnMessage implements ProtocolMessage {

	private long originNodeId;
	private long timestamp;

	public SaturnMessage(long originNodeId, long scalarTimestamp) {
		this.originNodeId = originNodeId;
		this.timestamp = scalarTimestamp;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getOriginNodeId() {
		return originNodeId;
	}

	public void setOriginNodeId(long originNodeId) {
		this.originNodeId = originNodeId;
	}

	@Override
	public int getSize() {
		return 0;
	}
}
