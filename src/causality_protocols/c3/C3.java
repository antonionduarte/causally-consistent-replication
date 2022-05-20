package causality_protocols.c3;

import peersim.core.Node;
import simulator.protocols.CausalityProtocol;
import simulator.protocols.messages.Message;
import simulator.protocols.messages.ProtocolMessage;

import java.util.List;

// maybe I need to write an initializer
// to build the initial VC.

/**
 * Lines 36-54:
 * of the Pseudocode are what I'm already doing with
 * my CausalityProtocol layer. [DONE]
 *
 * Lines 23-32:
 * write label from DS: propagate the operation to the
 * targets. [DONE]
 *
 * Lines
 */

public class C3 extends CausalityProtocol {

	long[] executingClock;
	long[] executedClock;
	long writeCounter; // probably not necessary

	List<Message> aheadExecutedOps;

	/**
	 * The constructor for the protocol.
	 */
	public C3(String prefix) {
		super(prefix);
	}

	@Override
	public Object clone() {
		return super.clone();
	}

	@Override
	public boolean verifyCausality(Message message) {
		ProtocolMessage c3Message = message.getProtocolMessage();

		// means the operation came from the local DS
		return c3Message == null;
	}

	@Override
	public void uponMessageExecuted(Node node, Message message) {
		// when a message ends execution
		// change the state of the message or create a new one?
		C3Message c3Message = (C3Message) message.getProtocolMessage();
		c3Message.setLblDeps(executingClock);

	}

	@Override
	public void uponMessageExecuting(Node node, Message message) {
		// if the message is from a local client/datastore, do nothing

		// line 60-64
		// executingClock <- executingClock[sourceDC] + 1;
	}
}
