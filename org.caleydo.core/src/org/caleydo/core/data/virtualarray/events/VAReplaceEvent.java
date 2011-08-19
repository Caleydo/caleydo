package org.caleydo.core.data.virtualarray.events;

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
public abstract class VAReplaceEvent<E extends VirtualArray<?, ?, ?>>
	extends AEvent {

	String vaType = null;
	E virtualArray;
	boolean usesVADirectly = false;

	int tableID = -1;

	/**
	 * default no-arg constructor.
	 */
	public VAReplaceEvent() {
		// nothing to initialize here
	}

	/**
	 * Constructor signaling which type of virtual array has to be updated
	 * 
	 * @param vaType
	 */
	public VAReplaceEvent(DataTable table, String dataDomainType, String vaType) {
		this.dataDomainID = dataDomainType;
		this.vaType = vaType;
		this.tableID = table.getID();
	}

	public VAReplaceEvent(DataTable table, String dataDomainType, String vaType, E virtualArray) {
		this.dataDomainID = dataDomainType;
		this.vaType = vaType;
		this.virtualArray = virtualArray;
		usesVADirectly = true;
		this.tableID = table.getID();
	}

	/**
	 * If no set is specified, the use case should send this to all suitable sets
	 * 
	 * @param idCategory
	 * @param vaType
	 * @param virtualArray
	 */
	protected VAReplaceEvent(String dataDomainType, String vaType, E virtualArray) {
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

	public int getTableID() {
		return tableID;
	}
}
