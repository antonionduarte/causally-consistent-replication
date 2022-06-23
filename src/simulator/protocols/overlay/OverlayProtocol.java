package simulator.protocols.overlay;

import peersim.config.Configuration;
import peersim.core.Protocol;

public abstract class OverlayProtocol implements Overlay, Protocol {

	private static final String PAR_PROT = "protocol";

	public static String protName;
	public static int pid;

	public OverlayProtocol(String prefix) {
		protName = (prefix.split("\\."))[1];
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
