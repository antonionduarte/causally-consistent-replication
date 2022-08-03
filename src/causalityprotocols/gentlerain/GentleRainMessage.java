package causalityprotocols.gentlerain;

import simulator.protocols.messages.Message;
import simulator.protocols.messages.ProtocolMessage;

public class GentleRainMessage implements ProtocolMessage {

	private long updateTime;
	private long item;
	private GentleRainClient client;

	/**
	 * The GST at a replica.
	 */
	private long globalStableTime;

	public GentleRainMessage(long updateTime, long item, GentleRainClient client) {
		this.updateTime = updateTime;
		this.item = item;
	}

	public long getItem() {
		return item;
	}

	public GentleRainClient getClient() {
		return client;
	}

	@Override
	public int getSize() {
		return 0;
	}
}
