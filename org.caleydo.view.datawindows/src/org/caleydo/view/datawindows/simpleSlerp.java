package org.caleydo.view.datawindows;


import org.caleydo.core.util.system.SystemTime;
import org.caleydo.core.util.system.Time;

public class simpleSlerp {
	public double speed;
	private double slerpFactor = 0;
	private Time time;
	public double state = 0;
	
	public simpleSlerp() {
		time = new SystemTime();
		((SystemTime) time).rebase();

		time.update();
		speed=1;
	}

	public boolean doASlerp() {

		slerpFactor = speed * time.deltaT();

		state = state + slerpFactor;
		
		if (state>=1){
			state = 1;
			return false;
		}
		time.update();

		return true;

	}

}
