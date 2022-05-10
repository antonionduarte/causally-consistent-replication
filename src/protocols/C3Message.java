package protocols;

import causality.messages.MessageAbstract;

public class C3Message extends MessageAbstract {

	long[] clock;
	C3MessageType c3MessageType;

	public C3Message(int delay, MessageType messageType, C3MessageType c3MessageType) {
		super(delay, messageType);
		this.c3MessageType = c3MessageType;
	}

	public long[] getClock() {
		return clock;
	}

	@Override
	public int getSize() {
		return 0;
	}
}
