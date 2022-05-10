package causality.messages;

public interface Message {

	enum MessageType {
		PROPAGATING,
		EXECUTING
	}

	MessageType getMessageType();

	void setMessageType(MessageType messageType);

	/**
	 * @return The size in Bytes of the Message's Payload.
	 */
	int getSize();

	/**
	 * Returns the delay of the message.
	 */
	int getExecutionTime();

	void setExecutionTime(int delay);

}

