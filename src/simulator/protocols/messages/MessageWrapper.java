package simulator.protocols.messages;

import peersim.core.Node;

/**
 * Wrapper for Messages.
 */
public class MessageWrapper implements Message {

	private ProtocolMessage protocolMessage;
	private OperationType operationType;
	private EventType eventType;


	private final long sendTime;
	private final long lastHop;

	private final Node originNode;
	private final String messageId;

	public MessageWrapper(
			OperationType operationType,
			EventType eventType,
			ProtocolMessage protocolMessage,
			Node node,
			long sendTime,
			long lastHop,
			String messageId
	) {
		this.protocolMessage = protocolMessage;
		this.operationType = operationType;
		this.eventType = eventType;
		this.lastHop = lastHop;
		this.originNode = node;
		this.sendTime = sendTime;
		this.messageId = messageId;
	}

	@Override
	public long getLastHop() {
		return lastHop;
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
	public EventType getEventType() {
		return this.eventType;
	}

	@Override
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	@Override
	public OperationType getOperationType() {
		return this.operationType;
	}

	@Override
	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
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
