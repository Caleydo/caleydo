package org.caleydo.core.event.view.selection;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.data.selection.ElementConnectionInformation;
import org.caleydo.core.event.AEvent;

/**
 * Signals the creation of a new selection.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class AddSelectionEvent
	extends AEvent {

	/** Related connectionID of the selection, might be legal in different views */
	private Integer connectionID;

	/** {@link ElementConnectionInformation} of the selection to add */
	private ElementConnectionInformation selectedElementRep;

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

	public ElementConnectionInformation getSelectedElementRep() {
		return selectedElementRep;
	}

	public void setSelectedElementRep(ElementConnectionInformation selectedElementRep) {
		this.selectedElementRep = selectedElementRep;
	}

}
