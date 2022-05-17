package simulator.overlay;

import peersim.config.Configuration;
import peersim.core.Protocol;

public abstract class OverlayProtocol implements Overlay, Protocol {

	public static int overlayPid;

	public OverlayProtocol(String prefix) {
		overlayPid = Configuration.getPid(prefix);
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
