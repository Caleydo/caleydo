package org.caleydo.view.matchmaker.event;

import org.caleydo.core.manager.event.AEvent;

/**
 * @author Alexander Lex
 * 
 */
public class UseFishEyeEvent extends AEvent {

	private boolean useFishEye;

	public UseFishEyeEvent() {
	}

	public UseFishEyeEvent(boolean useFishEye) {
		this.useFishEye = useFishEye;
	}

	public void setUseSorting(boolean useFishEye) {
		this.useFishEye = useFishEye;
	}

	public boolean isUseFishEye() {
		return useFishEye;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
