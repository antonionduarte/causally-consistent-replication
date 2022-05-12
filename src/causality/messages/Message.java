package causality.messages;

public interface Message {

	enum MessageType {
		WRITE,
		READ
	}

	boolean isPropagating();

	void togglePropagating();

	MessageType getMessageType();

	void setMessageType(MessageType messageType);

	ProtocolMessage getProtocolMessage();

	void setProtocolMessage(ProtocolMessage protocolMessage);

}
