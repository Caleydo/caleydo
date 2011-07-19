package org.caleydo.core.manager.event.data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.manager.event.AEvent;

/**
 * Event that signals that the virtual array has changed. VA users have to load the new one from the UseCase
 * if only the vaType is provided, or use the va attached.
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public abstract class ReplaceVAEvent<E extends VirtualArray<?, ?, ?>>
	extends AEvent {

	String vaType = null;
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
	public ReplaceVAEvent(DataTable set, String dataDomainType, String vaType) {
		this.dataDomainID = dataDomainType;
		this.vaType = vaType;
		this.setID = set.getID();
	}

	public ReplaceVAEvent(DataTable set, String dataDomainType, String vaType, E virtualArray) {
		this.dataDomainID = dataDomainType;
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
	protected ReplaceVAEvent(String dataDomainType, String vaType, E virtualArray) {
		this.dataDomainID = dataDomainType;
		this.vaType = vaType;
		this.virtualArray = virtualArray;
		usesVADirectly = true;
	}

	/**
	 * Returns the type of the VA which has to be replaced
	 * 
	 * @return
	 */
	public String getVaType() {
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
	public void setVAType(String vaType) {
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

	public boolean isUsesVADirectly() {
		return usesVADirectly;
	}

	public void setUsesVADirectly(boolean usesVADirectly) {
		this.usesVADirectly = usesVADirectly;
	}

	public void setVirtualArray(E virtualArray) {
		this.virtualArray = virtualArray;
	}

	public int getSetID() {
		return setID;
	}
}
