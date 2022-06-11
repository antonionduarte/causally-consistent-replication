package simulator.observers;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import simulator.protocols.CausalityProtocol;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ThroughputObserver implements Control {

	public static final String PATH = "./output/throughput/";
	public static final String EXPERIMENT_NAME = "EXPERIMENT_NAME";
	public static final String EXPERIMENT_TIME = "simulation.endtime";

	public ThroughputObserver(String prefix) {}

	@Override
	public boolean execute() {
		var experimentName = Configuration.getString(EXPERIMENT_NAME);
		var experimentTime = Configuration.getInt(EXPERIMENT_TIME);
		var filename = PATH + experimentName + ".txt";

		System.err.println(CommonState.getTime() + ": " + this.getClass().getName() + " extracting node throughput.");

		File throughputObservation = new File(filename);

		try (FileWriter fileWriter = new FileWriter(filename)) {
			if (throughputObservation.createNewFile()) {
				System.out.println("File created");
			}

			fileWriter.write("" + experimentTime + "\n");

			for (int i = 0; i < Network.size(); i++) {
				var node = Network.get(i);
				var protocol = (CausalityProtocol) node.getProtocol(Configuration.lookupPid(CausalityProtocol.protName));
				var executedOperations = protocol.getExecutedOperations();
				System.out.println("throughput-node-" + i + ": " + executedOperations);

				fileWriter.write("" + i + ",");
				fileWriter.write("" + executedOperations + "\n");

				System.out.println("DEBUG - Queue size - " + node.getID() + " - " + protocol.getOperationQueue().size());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		System.out.println();
		return false;
	}
}
