package org.caleydo.core.manager.event.data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.EVAType;
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.manager.event.AEvent;

/**
 * Event that signals that the virtual array has changed. VA users have to load the new one from the UseCase
 * if only the vaType is provided, or use the va attached.
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class ReplaceVirtualArrayEvent
	extends AEvent {

	EVAType vaType = null;
	EIDCategory idCategory = null;
	VirtualArray virtualArray;
	boolean usesVADirectly = false;

	/**
	 * default no-arg constructor.
	 */
	public ReplaceVirtualArrayEvent() {
		// nothing to initialize here
	}

	/**
	 * Constructor signaling which type of virtual array has to be updated
	 * 
	 * @param vaType
	 */
	public ReplaceVirtualArrayEvent(EIDCategory idCategory, EVAType vaType) {
		this.idCategory = idCategory;
		this.vaType = vaType;
	}

	public ReplaceVirtualArrayEvent(EIDCategory idCategory, EVAType vaType, VirtualArray virtualArray) {
		this.idCategory = idCategory;
		this.vaType = vaType;
		this.virtualArray = virtualArray;
		usesVADirectly = true;
	}

	/**
	 * Returns the id category for the virtual array to be replaced.
	 * 
	 * @return
	 */
	public EIDCategory getIDCategory() {
		return idCategory;
	}

	/**
	 * Returns the type of the VA which has to be replaced
	 * 
	 * @return
	 */
	public EVAType getVaType() {
		return vaType;
	}

	public VirtualArray getVirtualArray() {
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

	public void setIDCategory(EIDCategory idCategory) {
		this.idCategory = idCategory;
	}

	@Override
	public boolean checkIntegrity() {
		if (vaType == null || idCategory == null)
			return false;

		if (usesVADirectly)
			if (virtualArray == null)
				return false;

		return true;
	}

	public boolean isUsesVADirectly() {
		return usesVADirectly;
	}

	public void setUsesVADirectly(boolean usesVADirectly) {
		this.usesVADirectly = usesVADirectly;
	}

	public void setVaType(EVAType vaType) {
		this.vaType = vaType;
	}

	public void setVirtualArray(VirtualArray virtualArray) {
		this.virtualArray = virtualArray;
	}

}
