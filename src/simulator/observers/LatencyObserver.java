package simulator.observers;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import simulator.protocols.application.ApplicationProtocol;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Runs in the end of the simulation,
 * retrieves the Client perceived Latency statistics.
 */
public class LatencyObserver implements Control {

	public LatencyObserver(String prefix) {
	}

	private static final String PATH = "./output/latency/";
	private static final String PAR_EXPERIMENT_NAME = "EXPERIMENT_NAME";

	@Override
	public boolean execute() {
		var experimentName = Configuration.getString(PAR_EXPERIMENT_NAME);
		var filename = PATH + experimentName + ".txt";

		System.out.println(CommonState.getTime() + ": " + this.getClass().getName() + " extracting client perceived Latencies.");

		File latencyObservation = new File(filename);

		try (FileWriter fileWriter = new FileWriter(filename)) {
			if (latencyObservation.createNewFile()) {
				System.out.println("File created");
			}

			for (int i = 0; i < Network.size(); i++) {
				var node = Network.get(i);
				var application = (ApplicationProtocol) node.getProtocol(ApplicationProtocol.pid);
				var nodeLatencies = application.getMessageLatencies();
				System.out.print("perceived-latency-node-" + i + ": ");

				fileWriter.write("" + i);

				for (var latency : nodeLatencies) {
					System.out.print(latency + ",");
					fileWriter.write("," + latency);
				}

				fileWriter.write("\n");
				System.out.println();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return false;
	}
}
