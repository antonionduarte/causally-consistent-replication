package simulator.protocols;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;
import simulator.protocols.application.ApplicationProtocol;
import simulator.protocols.causality.CausalityProtocol;
import simulator.protocols.messages.Message;
import simulator.protocols.messages.MessageWrapper;

import java.util.LinkedList;
import java.util.Queue;

public class PendingEvents implements EDProtocol {

	private static final String PAR_EVENT_PROCESSING_TIME = "event_processing_time";
	private static final String PAR_MAX_PARALLEL_EVENTS = "max_parallel_events";

	private Queue<Message> pendingEvents;

	private final int eventProcessingTime;
	private final int maxParallelEvents;

	private long currentTimestamp;
	private int counterProcessedEvents;

	public static int pid;

	public PendingEvents(String prefix) {
		var protName = (prefix.split("\\."))[1];
		pid = Configuration.lookupPid(protName);
		maxParallelEvents = Configuration.getInt(prefix + "." + PAR_MAX_PARALLEL_EVENTS);
		eventProcessingTime = Configuration.getInt(prefix + "." + PAR_EVENT_PROCESSING_TIME);
	}

	@Override
	public Object clone() {
		try {
			PendingEvents clone = (PendingEvents) super.clone();
			clone.pendingEvents = new LinkedList<>();
			clone.currentTimestamp = -1;
			clone.counterProcessedEvents = 0;
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

			switch (next.getEventType()) {
				case PROPAGATING, EXECUTING -> {
					var causalityProtocol = (CausalityProtocol) node.getProtocol(CausalityProtocol.pid);
					CommonState.setPid(CausalityProtocol.pid);
					causalityProtocol.processEvent(node, CausalityProtocol.pid, next);
				}
				case RESPONSE -> {
					var applicationProtocol = (ApplicationProtocol) node.getProtocol(ApplicationProtocol.pid);
					CommonState.setPid(ApplicationProtocol.pid);
					applicationProtocol.processEvent(node, ApplicationProtocol.pid, next);
				}
			}
		} else {
			if (CommonState.getTime() > currentTimestamp) {
				this.currentTimestamp = CommonState.getTime();
				this.counterProcessedEvents = 0;
			}

			if ((maxParallelEvents != -1) && (counterProcessedEvents > maxParallelEvents)) {
				this.currentTimestamp++;
				this.counterProcessedEvents = 0;
			}

			this.pendingEvents.add(message);
			this.counterProcessedEvents++;

			var debug = CommonState.getTime();
			var sendInterval = currentTimestamp - CommonState.getTime();
			var sendDelay = sendInterval + eventProcessingTime;
			var nextMessage = new MessageWrapper(Message.EventType.NEXT);
			EDSimulator.add(sendDelay, nextMessage, CommonState.getNode(), PendingEvents.pid);
		}
	}
}
