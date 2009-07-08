package org.caleydo.core.manager.event.data;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;

/**
 * Event that signals that the virtual array has changed, and they are supposed to load the new one from the
 * UseCase
 * 
 * @author Alexander Lex
 */
public class ReplaceVirtualArrayEvent
	extends AEvent {

	public ReplaceVirtualArrayEvent(EVAType vaType) {
		this.vaType = vaType;
	}

	EVAType vaType = null;

	public EVAType getVaType() {
		return vaType;
	}

	public void setVAType(EVAType vaType) {
		this.vaType = vaType;
	}


	@Override
	public boolean checkIntegrity() {
		if (vaType == null)
			return false;
		else
			return true;
	}

}
