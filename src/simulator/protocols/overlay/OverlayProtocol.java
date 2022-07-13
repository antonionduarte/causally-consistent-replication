package simulator.protocols.overlay;

import peersim.config.Configuration;
import peersim.core.Protocol;

public abstract class OverlayProtocol implements Overlay, Protocol {

	public static int pid;

	public OverlayProtocol(String prefix) {
		var protName = (prefix.split("\\."))[1];
		pid = Configuration.lookupPid(protName);
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
