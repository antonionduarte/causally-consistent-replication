package causality.messages;

public abstract class MessageAbstract implements Message {

	private int delay;
	private MessageType messageType;

	public MessageAbstract(int delay, MessageType messageType) {
		this.delay = delay;
		this.messageType = messageType;
	}

	@Override
	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	@Override
	public int getExecutionTime() {
		return delay;
	}

	@Override
	public void setExecutionTime(int delay) {
		this.delay = delay;
	}

	@Override
	public abstract int getSize();
}
