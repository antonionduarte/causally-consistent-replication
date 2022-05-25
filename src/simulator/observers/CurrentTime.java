package simulator.observers;

import peersim.core.CommonState;
import peersim.core.Control;

/**
 * Simple prints out the current simulation time periodically.
 */
public class CurrentTime implements Control {

	public CurrentTime(String prefix) {

	}

	@Override
	public boolean execute() {
		System.err.println("Current simulated time: " + CommonState.getTime());
		return false;
	}

}
