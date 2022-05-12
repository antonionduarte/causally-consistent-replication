package causality.messages;

import peersim.core.Protocol;

/**
 * Wrapper for Messages.
 */
public class MessageWrapper implements Message {

	ProtocolMessage protocolMessage;
	MessageType messageType;
	boolean isPropagating;

	public MessageWrapper(MessageType messageType, ProtocolMessage protocolMessage) {
		this.protocolMessage = protocolMessage;
		this.messageType = messageType;
		this.isPropagating = false;
	}

	@Override
	public boolean isPropagating() {
		return isPropagating;
	}

	public void togglePropagating() {
		this.isPropagating = !isPropagating;
	}

	@Override
	public MessageType getMessageType() {
		return messageType;
	}

	@Override
	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	@Override
	public ProtocolMessage getProtocolMessage() {
		return protocolMessage;
	}

	public void setProtocolMessage(ProtocolMessage protocolMessage) {
		this.protocolMessage = protocolMessage;
	}
}
