package org.caleydo.core.manager.event.view.selection;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.manager.event.AEvent;

/**
 *  
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class AddSelectionEvent
	extends AEvent {

	private Integer connectionID;
	
	private SelectedElementRep selectedElementRep;

	@Override
	public boolean checkIntegrity() {
		// TODO Auto-generated method stub
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
