package simulator.protocols;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;
import simulator.protocols.causality.CausalityProtocol;
import simulator.protocols.messages.Message;
import simulator.protocols.messages.MessageWrapper;

import java.util.LinkedList;
import java.util.Queue;

public class PendingEvents implements EDProtocol {

	public static final String EVENT_PROCESSING_TIME = "EVENT_PROCESSING_TIME";

	private Queue<Message> pendingEvents;
	private final int eventProcessingTime;
	public static String protName;


	public PendingEvents(String prefix) {
		protName = (prefix.split("\\."))[1];
		eventProcessingTime = Configuration.getInt(EVENT_PROCESSING_TIME);
	}

	@Override
	public Object clone() {
		try {
			PendingEvents clone = (PendingEvents) super.clone();
			clone.pendingEvents = new LinkedList<>();
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void processEvent(Node node, int pid, Object event) {
		var message = (Message) event;

		if (message.getEventType() == Message.EventType.NEXT) {
			var next = this.pendingEvents.remove();
			var causalityPid = Configuration.lookupPid(CausalityProtocol.protName);
			var causalityProtocol = (CausalityProtocol) node.getProtocol(causalityPid);

			causalityProtocol.processEvent(node, pid, next);
			var nextMessage = new MessageWrapper(null, Message.EventType.NEXT, null, null, 0, 0, null);

			if (!pendingEvents.isEmpty())
				EDSimulator.add(eventProcessingTime, nextMessage, CommonState.getNode(), CommonState.getPid());
		} else {
			this.pendingEvents.add(message);
			var nextMessage = new MessageWrapper(null, Message.EventType.NEXT, null, null, 0, 0, null);
			EDSimulator.add(eventProcessingTime, nextMessage, CommonState.getNode(), CommonState.getPid());
		}
	}
}
