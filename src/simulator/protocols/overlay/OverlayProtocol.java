package simulator.protocols.overlay;

import peersim.config.Configuration;
import peersim.core.Protocol;

public abstract class OverlayProtocol implements Overlay, Protocol {

	public static String protName;

	public OverlayProtocol(String prefix) {
		protName = (prefix.split("\\."))[1];
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
