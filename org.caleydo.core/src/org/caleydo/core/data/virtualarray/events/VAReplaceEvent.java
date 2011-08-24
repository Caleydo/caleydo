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

	private E virtualArray;
	private String perspectiveID = null;

	/**
	 * default no-arg constructor.
	 */
	public VAReplaceEvent() {
		// nothing to initialize here
	}

	/**
	 * If no set is specified, the use case should send this to all suitable sets
	 * 
	 * @param idCategory
	 * @param perspectiveID
	 * @param virtualArray
	 */
	protected VAReplaceEvent(String dataDomainID, String perspectiveID, E virtualArray) {
		this.dataDomainID = dataDomainID;
		this.perspectiveID = perspectiveID;
		this.virtualArray = virtualArray;
	}

	public E getVirtualArray() {
		return virtualArray;
	}

	@Override
	public boolean checkIntegrity() {
		if (dataDomainID == null || perspectiveID == null)
			return false;

		return true;
	}

	public void setVirtualArray(E virtualArray) {
		this.virtualArray = virtualArray;
	}

	public String getPerspectiveID() {
		return perspectiveID;
	}
}
