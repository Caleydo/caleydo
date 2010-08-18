package org.caleydo.view.datawindows;

import org.caleydo.core.util.system.SystemTime;
import org.caleydo.core.util.system.Time;

public class SimpleSlerp {
	public float speed;
	private float slerpFactor = 0;
	private Time time;
	public float state = 0;
	public float endingCondition;
	public float relativeState = 0;

	public SimpleSlerp() {
		time = new SystemTime();
		((SystemTime) time).rebase();

		time.update();
		speed = 1;
		endingCondition = 1;
	}

	public boolean doASlerp() {

		slerpFactor = speed * (float) time.deltaT();

		if (endingCondition >= 0) {
			relativeState = state - relativeState;

			state = state + slerpFactor;
			if (state >= endingCondition) {
				state = endingCondition;
				return false;
			}
		} else {
			relativeState = state + relativeState;

			state = state - slerpFactor;
			if (state <= endingCondition) {
				state = endingCondition;
				return false;
			}
		}
		time.update();
		return true;
	}

}
