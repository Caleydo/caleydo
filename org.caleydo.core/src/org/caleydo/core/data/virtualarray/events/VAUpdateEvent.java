package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.event.AEvent;

/**
 * <p>
 * Base class for Virtual Array Updates. These events are intended to provide information which virtual array
 * has changed. Receivers are expected to go to their data structure and reload the VA from there.
 * </p>
 * <p>
 * This is intended to be created and published only by instances managing the data structures, such as
 * {@link ATableBasedDataDomain}.
 * </p>
 * 
 * @author Alexander Lex
 */
public abstract class VAUpdateEvent
	extends AEvent {

	/** additional information about the selection, e.g. to display in the info-box */
	private String info;
	/** the id of the associated {@link DataTable} */
	private int dataTableID = -1;

	/**
	 * Set the ID of the {@link DataTable} the virtual array to be updated is associated with
	 * 
	 * @param dataTableID
	 */
	public void setDataTableID(int dataTableID) {
		this.dataTableID = dataTableID;
	}

	/**
	 * Get the ID of the {@link DataTable} the virtual array to be updated is associated with
	 * 
	 * @return the id of the associated {@link DataTable}
	 */
	public int getDataTableID() {
		return dataTableID;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public boolean checkIntegrity() {
		if (dataTableID < 0)
			return false;
		return true;
	}

}
