package org.caleydo.core.manager.event.data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.IVAType;
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
public abstract class ReplaceVAEvent<E extends VirtualArray<?, ?, ?, ?>, T extends IVAType>
	extends AEvent {

	T vaType = null;
	EIDCategory idCategory = null;
	E virtualArray;
	boolean usesVADirectly = false;

	int setID = -1;

	/**
	 * default no-arg constructor.
	 */
	public ReplaceVAEvent() {
		// nothing to initialize here
	}

	/**
	 * Constructor signaling which type of virtual array has to be updated
	 * 
	 * @param vaType
	 */
	public ReplaceVAEvent(ISet set, EIDCategory idCategory, T vaType) {
		this.idCategory = idCategory;
		this.vaType = vaType;
		this.setID = set.getID();
	}

	public ReplaceVAEvent(ISet set, EIDCategory idCategory, T vaType, E virtualArray) {
		this.idCategory = idCategory;
		this.vaType = vaType;
		this.virtualArray = virtualArray;
		usesVADirectly = true;
		this.setID = set.getID();
	}

	/**
	 * If no set is specified, the use case should send this to all suitable sets
	 * 
	 * @param idCategory
	 * @param vaType
	 * @param virtualArray
	 */
	protected ReplaceVAEvent(EIDCategory idCategory, T vaType, E virtualArray) {
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
	public T getVaType() {
		return vaType;
	}

	public E getVirtualArray() {
		return virtualArray;
	}

	/**
	 * Set the type of the VA which has to be replaced
	 * 
	 * @param vaType
	 */
	public void setVAType(T vaType) {
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

	public void setVaType(T vaType) {
		this.vaType = vaType;
	}

	public void setVirtualArray(E virtualArray) {
		this.virtualArray = virtualArray;
	}

	public int getSetID() {
		return setID;
	}
}
