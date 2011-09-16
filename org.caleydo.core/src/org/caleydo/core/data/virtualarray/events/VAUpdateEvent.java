package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.ADataPerspective;
import org.caleydo.core.event.AEvent;

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

	/** the id of the associated {@link DataTable} */
	private String perspectiveID = null;

	/**
	 * Set the ID of the {@link ADataPerspective} the virtual array to be updated is associated with
	 * 
	 * @param perspectiveID
	 */
	public void setPerspectiveID(String perspectiveID) {
		this.perspectiveID = perspectiveID;
	}

	/**
	 * Get the ID of the {@link DataTable} the virtual array to be updated is associated with
	 * 
	 * @return the id of the associated {@link DataTable}
	 */
	public String getPerspectiveID() {
		return perspectiveID;
	}

	@Override
	public boolean checkIntegrity() {
		if (perspectiveID == null)
			return false;
		return true;
	}

}
