package causalityprotocols.saturn;

import simulator.protocols.messages.ProtocolMessage;

public class SaturnMessage implements ProtocolMessage {

	private long receivedFromId;
	private long originNodeId;
	private long timestamp;

	public SaturnMessage(long receivedFromId, long originNodeId, long scalarTimestamp) {
		this.receivedFromId = receivedFromId;
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

	public long getReceivedFromId() {
		return receivedFromId;
	}

	public void setReceivedFromId(long receivedFrom) {
		this.receivedFromId = receivedFrom;
	}

	@Override
	public int getSize() {
		return 0;
	}
}
