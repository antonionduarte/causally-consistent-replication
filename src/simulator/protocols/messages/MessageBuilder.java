package simulator.protocols.messages;

import peersim.core.Node;

public class MessageBuilder {
	private ProtocolMessage protocolMessage;
	private Message.OperationType operationType;
	private Message.EventType eventType;

	private Node originNode;
	private String messageId;

	private long sendTime;
	private long lastHop;
	private long migrationTarget;
	private char partition;

	public MessageBuilder() {
		this.protocolMessage = null;
		this.operationType = null;
		this.eventType = null;
		this.sendTime = -1;
		this.lastHop = -1;
		this.migrationTarget = -1;
		this.originNode = null;
		this.messageId = null;
	}

	public Message build() {
		return new MessageWrapper(this);
	}

	public char getPartition() {
		return partition;
	}

	public MessageBuilder setPartition(char partition) {
		this.partition = partition;
		return this;
	}

	public long getMigrationTarget() {
		return migrationTarget;
	}

	public MessageBuilder setMigrationTarget(long migrationTarget) {
		this.migrationTarget = migrationTarget;
		return this;
	}

	public ProtocolMessage getProtocolMessage() {
		return protocolMessage;
	}

	public MessageBuilder setProtocolMessage(ProtocolMessage protocolMessage) {
		this.protocolMessage = protocolMessage;
		return this;
	}

	public Message.OperationType getOperationType() {
		return operationType;
	}

	public MessageBuilder setOperationType(Message.OperationType operationType) {
		this.operationType = operationType;
		return this;
	}

	public Message.EventType getEventType() {
		return eventType;
	}

	public MessageBuilder setEventType(Message.EventType eventType) {
		this.eventType = eventType;
		return this;
	}

	public long getSendTime() {
		return sendTime;
	}

	public MessageBuilder setSendTime(long sendTime) {
		this.sendTime = sendTime;
		return this;
	}

	public long getLastHop() {
		return lastHop;
	}

	public MessageBuilder setLastHop(long lastHop) {
		this.lastHop = lastHop;
		return this;
	}

	public Node getOriginNode() {
		return originNode;
	}

	public MessageBuilder setOriginNode(Node originNode) {
		this.originNode = originNode;
		return this;
	}

	public String getMessageId() {
		return messageId;
	}

	public MessageBuilder setMessageId(String messageId) {
		this.messageId = messageId;
		return this;
	}
}
