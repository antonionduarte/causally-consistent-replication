package causalityprotocols.gentlerain;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import simulator.protocols.application.ApplicationProtocol;
import simulator.protocols.messages.Message;

import java.util.HashMap;
import java.util.Map;

public class GentleRainApplication extends ApplicationProtocol {

	private int numberClients;
	private int numberItems;

	/**
	 * Stores the current updateTime for a client.
	 */
	private Map<String, GentleRainClient> clients;


	private static final String PAR_NUMBER_CLIENTS = "number_clients";

	public GentleRainApplication(String prefix) {
		super(prefix);
		this.numberItems = 100000;
		this.numberClients = Configuration.getInt(prefix + "." + PAR_NUMBER_CLIENTS);
	}

	@Override
	public Object clone() {
		GentleRainApplication gentleRainApplication = (GentleRainApplication) super.clone();
		gentleRainApplication.clients = new HashMap<>();

		for (var i = 0; i < numberClients; i++) {
			String id = CommonState.getNode().getID() + "_" + i;
			var client = new GentleRainClient(id, 0L, 0L);
			gentleRainApplication.clients.put(id, client);
		}

		return gentleRainApplication;
	}

	@Override
	public void uponReceiveMessage(Node node, Message message) {
		var protocolMessage = (GentleRainMessage) message.getProtocolMessage();
		var client = protocolMessage.getClient();
		client.setOccupied(false);
		if (message.getOperationType() == Message.OperationType.MIGRATION) {
			this.clients.put(client.getClientId(), client);
		}
	}

	@Override
	public void changeInitialMessage(Node node, Message message) {
		this.uponSendMessage(node, message);
	}

	@Override
	public void changeResponseMessage(Node node, Message message) {
		this.uponSendMessage(node, message);
	}

	private void uponSendMessage(Node node, Message message) {
		var clients = this.clients.values();
		var item = CommonState.random.nextLong(numberItems);
		GentleRainClient client = null;

		for (var elem : clients) {
			if (!elem.isOccupied()) {
				client = elem;
				break;
			}
		}

		client.setOccupied(true);
		var protocolMessage = new GentleRainMessage(0L, item, client);
		if (message.getOperationType() == Message.OperationType.MIGRATION) {
			this.clients.remove(client.getClientId());
		}
		message.setProtocolMessage(protocolMessage);
	}
}
