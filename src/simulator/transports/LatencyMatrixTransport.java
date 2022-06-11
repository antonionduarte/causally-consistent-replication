package simulator.transports;

import peersim.config.Configuration;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;
import simulator.transports.utils.ReadLatencyMatrix;

public class LatencyMatrixTransport implements Transport {

	public static final String LATENCY_FILE_CONFIG = "latency_path";

	long[][] latencyMatrix;

	public LatencyMatrixTransport(String prefix) {
		var latencyPath = Configuration.getString(prefix + "." + LATENCY_FILE_CONFIG);
		this.latencyMatrix = ReadLatencyMatrix.readLatencyMatrix(latencyPath);
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void send(Node src, Node dest, Object msg, int pid) {
		var latency = getLatency(src, dest);
		EDSimulator.add(latency, msg, dest, pid);
	}

	@Override
	public long getLatency(Node src, Node dest) {
		return latencyMatrix[(int) src.getID()][(int) dest.getID()];
	}
}
