package org.caleydo.core.event.view.selection;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.view.vislink.ISelectionTransformer;

/**
 * Event to signal to {@link ISelectionTransformer}s that new selections have been created to transform.
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