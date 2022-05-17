package simulator.messages;

import peersim.core.Node;

/**
 * Wrapper for Messages.
 */
public class MessageWrapper implements Message {

	private ProtocolMessage protocolMessage;
	private MessageType messageType;

	private boolean isPropagating;

	private final long sendTime;
	private final Node originNode;
	private final String messageId;

	public MessageWrapper(MessageType messageType, ProtocolMessage protocolMessage, Node node, long sendTime, String messageId) {
		this.protocolMessage = protocolMessage;
		this.messageType = messageType;
		this.isPropagating = false;
		this.originNode = node;
		this.sendTime = sendTime;
		this.messageId = messageId;
	}

	@Override
	public String getMessageId() {
		return messageId;
	}

	@Override
	public long getSendTime() {
		return this.sendTime;
	}

	@Override
	public Node getOriginNode() {
		return this.originNode;
	}

	@Override
	public boolean isPropagating() {
		return isPropagating;
	}

	@Override
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

	@Override
	public void setProtocolMessage(ProtocolMessage protocolMessage) {
		this.protocolMessage = protocolMessage;
	}
}
