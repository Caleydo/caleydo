package org.caleydo.core.manager.event.view.selection;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.manager.event.AEvent;

/**
 * Signals the creation of a new selection.
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class AddSelectionEvent
	extends AEvent {

	/** Related connectionID of the selection, might be legal in different views */
	private Integer connectionID;
	
	/** {@link SelectedElementRep} of the selection to add */
	private SelectedElementRep selectedElementRep;

	@Override
	public boolean checkIntegrity() {
		if (connectionID == null || selectedElementRep == null) {
			return false;
		}
		return true;
	}

	public Integer getConnectionID() {
		return connectionID;
	}

	public void setConnectionID(Integer connectionID) {
		this.connectionID = connectionID;
	}

	public SelectedElementRep getSelectedElementRep() {
		return selectedElementRep;
	}

	public void setSelectedElementRep(SelectedElementRep selectedElementRep) {
		this.selectedElementRep = selectedElementRep;
	}

}
