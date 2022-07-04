package simulator.protocols.messages;

import peersim.core.Node;

public interface Message {

	/**
	 * @return The id of the message
	 */
	String getMessageId();

	/**
	 * @return The time at which the message was sent.
	 */
	long getSendTime();

	/**
	 * @return The nodeID of the last node this message hopped on.
	 */
	long getLastHop();

	/**
	 * @return The node that the message came from.
	 */
	Node getOriginNode();

	/**
	 * @return The type of the event, either Propagating, Executing or Migrating.
	 */
	EventType getEventType();

	/**
	 * Sets the event type.
	 */
	void setEventType(EventType eventType);

	/**
	 * Messages in the system are either Write messages or Read messages.
	 */
	OperationType getOperationType();

	/**
	 * Changes the type of message.
	 */
	void setOperationType(OperationType operationType);

	/**
	 * @return The partition that the message is destined to.
	 */
	Character getPartition();

	/**
	 * @return The id of the node to migrate to.
	 */
	long getMigrationTarget();

	/**
	 * The Message class acts as a wrapper for classes specific to Protocols.
	 *
	 * @return The wrapped Protocol specific object.
	 */
	ProtocolMessage getProtocolMessage();

	/**
	 * @param protocolMessage Sets the wrapped protocolMessage class.
	 */
	void setProtocolMessage(ProtocolMessage protocolMessage);

	enum OperationType {
		WRITE,
		READ,
		MIGRATION
	}

	enum EventType {
		PROPAGATING,
		EXECUTING,
		RESPONSE,
		NEXT
	}
}
