package causality_protocols.c3;

import peersim.core.Node;
import simulator.protocols.CausalityProtocol;
import simulator.protocols.messages.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	/**
	 * Maps from nodeId -> clockValue
	 */
	private Map<Long, Long> executingClock;
	private Map<Long, Long> executedClock;
	private long writeCounter; // probably not necessary

	private List<Message> aheadExecutedOps;

	/**
	 * The constructor for the protocol.
	 */
	public C3(String prefix) {
		super(prefix);
	}

	@Override
	public Object clone() {
		C3 clone = (C3) super.clone();
		clone.executedClock = new HashMap<>();
		clone.executingClock = new HashMap<>();
		clone.writeCounter = 0;
		return super.clone();
	}

	@Override
	public boolean verifyCausality(Node node, Message message) {
		C3Message wrappedMessage = (C3Message) message.getProtocolMessage();

		// means message came from local DS
		if (wrappedMessage == null) return true;

		Map<Long, Long> messageDeps = wrappedMessage.getLblDeps();

		for (long nodeId : messageDeps.keySet()) {
			// se uma das entries no executingClock for maior, quer dizer que either é || ou >

			// tenho de verificar se todas as entries são maiores, -> true
			// ou se são todas iguais -> true
			// ou se são um misto -> true

			// else -> false
			if (executingClock.containsKey(nodeId)) {
				if (executingClock.get(nodeId) > messageDeps.get(nodeId)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void uponMessageExecuted(Node node, Message message) {
		// when a message ends execution
		// change the state of the message or create a new one?

		// prob update executed clock
		C3Message c3Message = (C3Message) message.getProtocolMessage();
		c3Message.setLblDeps(executingClock);

	}

	@Override
	public void uponMessageExecuting(Node node, Message message) {
		// prob update executing clock


		// if the message is from a local client/datastore, do nothing

		// line 60-64
		// executingClock <- executingClock[sourceDC] + 1;
	}
}
