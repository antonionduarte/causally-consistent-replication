package protocols;

import causality.CausalityProtocolAbstract;
import causality.messages.Message;
import peersim.config.Configuration;

public class C3 extends CausalityProtocolAbstract {

	long[] executingClock;
	long[] executedClock;

	long writeCounter;

	/**
	 *
	 * operationCounter: used to timeStamp operations.
	 * executedClock: vectorClock with the latest executed operation from each datacenter.
	 * executingClock: vectorClock with the timestamp of the latest operation from each DataCenter.
	 * NOTE: executingClock >= executedClock.
	 *
	 * When an Application issues a write, the client sends the operation to the DataStore, which forwards
	 * info about the operation to the Causality Layer. And forwards it to the DataStore nodes that are responsible for
	 * executing the write.
	 *
	 */

	public C3() {
		var numberNodes = Configuration.getInt("SIZE");

		this.executedClock = new long[numberNodes];
		this.executingClock = new long[numberNodes];

		for (int i = 0; i <= numberNodes; i++) {
			this.executingClock[i] = 0;
			this.executedClock[i] = 0;
		}

		this.writeCounter = 0;
	}

	@Override
	public Object clone() {
		return super.clone();
	}

	@Override
	public boolean verifyCausality(Message message) {
		var receivedMessage = (C3Message) message;
		var receivedClock = receivedMessage.getClock();


		return false;
	}

	@Override
	public void processProtocolMessage(Message message) {

	}

	@Override
	public void messageExecutingProtocol(Message message) {

	}
}
