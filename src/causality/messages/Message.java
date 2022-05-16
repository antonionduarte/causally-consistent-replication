package causality.messages;

import peersim.core.Node;

public interface Message {


	enum MessageType {
		WRITE,
		READ;

	}


	String getMessageId();

	/**
	 * @return The time at which the message was sent.
	 */
	long getSendTime();

	/**
	 * @return The node that the message came from.
	 */
	Node getOriginNode();

	/**
	 * @return State of the message, it's either propagating or executing.
	 */
	boolean isPropagating();

	/**
	 * Toggles if the message is propagating or not (not being executing)
	 */
	void togglePropagating();

	/**
	 * Messages in the system are either Write messages or Read messages.
	 */
	MessageType getMessageType();

	/**
	 * Changes the type of message.
	 */
	void setMessageType(MessageType messageType);

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

}
