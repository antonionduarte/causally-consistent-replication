package simulator.observers.visibility;

import peersim.core.Control;

import java.util.List;

public class VisibilityObserver implements Control {

	List<List<Long>> visibilityTimes;

	@Override
	public boolean execute() {
		return false;
	}
}
