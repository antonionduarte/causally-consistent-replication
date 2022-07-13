package simulator.protocols.causality;

import peersim.core.Node;
import peersim.edsim.EDProtocol;
import simulator.protocols.messages.Message;

public interface Causality extends EDProtocol {

	/**
	 * Verifies if it's possible to execute an operation according to causal consistency.
	 *
	 * @param message The operation to verify.
	 * @param node The local node.
	 * @return Boolean indicating if it's possible to execute the operation.
	 */
	boolean checkCausality(Node node, Message message);

	/**
	 * Processes a Protocol specific Message when it's possible according to causal consistency.
	 *
	 * @param message The message / message to process.
	 * @param node The local node.
	 */
	void operationFinishedExecution(Node node, Message message);

	/**
	 * If a protocol needs to change it's internal state when it places a message in execution.
	 *
	 * @param message The message that was placed in execution.
	 * @param node The local node.
	 */
	void operationStartedExecution(Node node, Message message);
}
