package org.caleydo.core.event.view.selection;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.event.AEvent;

/**
 * Event to signal that all selections for a specific {@link EIDType} should be deleted.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class ClearConnectionsEvent
	extends AEvent {

	/** selection type to be deleted */
	private IDType idType;

	@Override
	public boolean checkIntegrity() {
		if (idType == null) {
			return false;
		}
		return true;
	}

	public IDType getIdType() {
		return idType;
	}

	public void setIdType(IDType idType) {
		this.idType = idType;
	}

}
