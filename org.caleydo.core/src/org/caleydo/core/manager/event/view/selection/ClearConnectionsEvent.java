package org.caleydo.core.manager.event.view.selection;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.event.AEvent;

/**
 *  
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class ClearConnectionsEvent
	extends AEvent {

	private EIDType idType;

	@Override
	public boolean checkIntegrity() {
		// TODO Auto-generated method stub
		return true;
	}

	public EIDType getIdType() {
		return idType;
	}

	public void setIdType(EIDType idType) {
		this.idType = idType;
	}

}
