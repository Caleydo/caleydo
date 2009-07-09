package org.caleydo.core.manager.event.data;

import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;

/**
 * Event that signals that the virtual array has changed. VA users have to load the new one from the UseCase if only the vaType is provided, or use the va attached.
 * 
 * @author Alexander Lex
 */
public class ReplaceVirtualArrayEvent
	extends AEvent {

	EVAType vaType = null;
	IVirtualArray virtualArray;
	boolean usesVADirectly = false;

	/**
	 * Constructor signaling which type of virtual array has to be updated
	 * 
	 * @param vaType
	 */
	public ReplaceVirtualArrayEvent(EVAType vaType) {
		this.vaType = vaType;
	}

	public ReplaceVirtualArrayEvent(EVAType vaType, IVirtualArray virtualArray) {
		this.vaType = vaType;
		this.virtualArray = virtualArray;
		usesVADirectly = true;
	}

	/**
	 * Returns the type of the VA which has to be replaced
	 * 
	 * @return
	 */
	public EVAType getVaType() {
		return vaType;
	}

	public IVirtualArray getVirtualArray() {
		return virtualArray;
	}

	/**
	 * Set the type of the VA which has to be replaced
	 * 
	 * @param vaType
	 */
	public void setVAType(EVAType vaType) {
		this.vaType = vaType;
	}

	@Override
	public boolean checkIntegrity() {
		if (vaType == null)
			return false;
		if (usesVADirectly)
			if (virtualArray == null)
				return false;

		return true;
	}

}
