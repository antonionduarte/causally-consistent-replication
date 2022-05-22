package simulator.protocols;

import simulator.protocols.messages.Message;
import peersim.core.Node;
import peersim.edsim.EDProtocol;

public interface Causality extends EDProtocol {

	/**
	 * Processes an operation by deciding its execution time, and sending an event to self.
	 *
	 * @param node    The local Node.
	 * @param message The Operation to execute.
	 * @param pid     The identifier of this protocol.
	 */
	void executeOperation(Node node, Message message, int pid);

	/**
	 * Verifies if it's possible to execute an operation according to causal consistency.
	 *
	 * @param message The operation to verify.
	 * @param node The local node.
	 * @return Boolean indicating if it's possible to execute the operation.
	 */
	boolean verifyCausality(Node node, Message message);

	/**
	 * Processes a Protocol specific Message when it's possible according to causal consistency.
	 *
	 * @param message The message / message to process.
	 * @param node The local node.
	 */
	void uponOperationExecuted(Node node, Message message);

	/**
	 * If a protocol needs to change it's internal state when it places a message in execution.
	 *
	 * @param message The message that was placed in execution.
	 * @param node The local node.
	 */
	void uponOperationExecuting(Node node, Message message);

	/**
	 * Processes the operation Queue, checking if any operation that was in the Queue is now possible according to
	 * causal consistency.
	 *
	 * @param node The local Node.
	 */
	void processQueue(Node node, int pid);

}