package org.caleydo.core.event.view;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.event.AEvent;

/**
 * Event signaling that the data representation should be changed (see {@link DataRepresentation})
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
