package causalityprotocols.gentlerain;

import peersim.core.Node;
import simulator.protocols.application.ApplicationProtocol;
import simulator.protocols.messages.Message;

public class GentleRainApplication extends ApplicationProtocol {

	/**
	 * Map from Long -> Long,
	 * represents objectId / index, current GST. 
	 */ 
	Map<Long, Long> objects;

	public 

	public GentleRainApplication(String prefix) {
		super(prefix);
	}

	@Override
	public void changeInitialMessage(Node node, Message message) {

	}

	@Override
	public void changeResponseMessage(Node node, Message message) {

	}
}
