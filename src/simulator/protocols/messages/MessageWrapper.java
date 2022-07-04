package simulator.protocols.messages;

import peersim.core.Node;

/**
 * Wrapper for Messages.
 */
public class MessageWrapper implements Message {

	private final long sendTime;
	private final long lastHop;
	private final long migrationTarget;
	private final char partition;
	private final Node originNode;
	private final String messageId;
	private ProtocolMessage protocolMessage;
	private OperationType operationType;
	private EventType eventType;


	public MessageWrapper(Message message, EventType eventType, Node node) {
		this.protocolMessage = message.getProtocolMessage();
		this.eventType = eventType;
		this.lastHop = node.getID();
		this.operationType = message.getOperationType();
		this.sendTime = message.getSendTime();
		this.messageId = message.getMessageId();
		this.originNode = message.getOriginNode();
		this.migrationTarget = message.getMigrationTarget();
		this.partition = message.getPartition();
	}

	public MessageWrapper(EventType eventType) {
		this.protocolMessage = null;
		this.eventType = eventType;
		this.lastHop = -1;
		this.operationType = null;
		this.sendTime = -1;
		this.migrationTarget = -1;
		this.messageId = null;
		this.originNode = null;
		this.partition = '0';
	}

	public MessageWrapper(MessageBuilder messageBuilder) {
		this.protocolMessage = messageBuilder.getProtocolMessage();
		this.operationType = messageBuilder.getOperationType();
		this.eventType = messageBuilder.getEventType();
		this.lastHop = messageBuilder.getLastHop();
		this.sendTime = messageBuilder.getSendTime();
		this.originNode = messageBuilder.getOriginNode();
		this.messageId = messageBuilder.getMessageId();
		this.migrationTarget = messageBuilder.getMigrationTarget();
		this.partition = messageBuilder.getPartition();
	}

	@Override
	public Character getPartition() {
		return this.partition;
	}

	@Override
	public long getMigrationTarget() {
		return this.migrationTarget;
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
