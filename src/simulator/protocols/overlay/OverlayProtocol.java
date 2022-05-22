package simulator.protocols.overlay;

import peersim.config.Configuration;
import peersim.core.Protocol;

public abstract class OverlayProtocol implements Overlay, Protocol {

	public static String overlayPrefix;

	public OverlayProtocol(String prefix) {
		overlayPrefix = prefix;
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
