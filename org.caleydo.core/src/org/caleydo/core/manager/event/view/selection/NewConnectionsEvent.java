package org.caleydo.core.manager.event.view.selection;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 *  
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class NewConnectionsEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// TODO Auto-generated method stub
		return true;
	}

}
