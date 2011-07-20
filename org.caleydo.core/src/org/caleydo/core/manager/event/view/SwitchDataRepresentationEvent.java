package org.caleydo.core.manager.event.view;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.manager.event.AEvent;

/**
 * Event signaling that the data representation should be changed (see {@link EDataRepresentation})
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class SwitchDataRepresentationEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		// nothing to check
		return true;
	}
}
