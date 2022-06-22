package simulator.observers;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import simulator.protocols.causality.CausalityProtocol;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisibilityObserver implements Control {

	public static final String PATH = "./output/visibility/";
	public static final String EXPERIMENT_NAME = "EXPERIMENT_NAME";


	public VisibilityObserver(String prefix) {}

	@Override
	public boolean execute() {
		Map<String, List<Long>> visibilityTimes = new HashMap<>();

		System.err.println(CommonState.getTime() + ": " + this.getClass().getName() + " extracting Message Visibility Times.");

		for (int i = 0; i < Network.size(); i++) {
			var node = Network.get(i);
			var protocol = (CausalityProtocol) node.getProtocol(Configuration.lookupPid(CausalityProtocol.protName));
			var messageVisibilities = protocol.getVisibilityTimes();

			for (var messageId : messageVisibilities.keySet()) {
				if (!visibilityTimes.containsKey(messageId)) {
					var visibilityList = new ArrayList<Long>();
					visibilityList.add(messageVisibilities.get(messageId));
					visibilityTimes.put(messageId, visibilityList);
				} else {
					visibilityTimes.get(messageId).add(messageVisibilities.get(messageId));
				}
			}
		}

		var experimentName = Configuration.getString(EXPERIMENT_NAME);
		var filename = PATH + experimentName + ".txt";

		File visibilityObservation = new File(filename);

		 try (FileWriter fileWriter = new FileWriter(filename)) {
			 if (visibilityObservation.createNewFile()) {
				 System.out.println("File created");
			 }

			 for (var message : visibilityTimes.keySet()) {
				 for (var time : visibilityTimes.get(message)) {
					 fileWriter.write(time + ",");
				 }
				 fileWriter.write('\n');
			 }
		 } catch (IOException e) {
			 throw new RuntimeException(e);
		 }

		return false;
	}
}
