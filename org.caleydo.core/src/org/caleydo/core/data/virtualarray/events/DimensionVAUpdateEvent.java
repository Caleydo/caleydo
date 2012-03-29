package org.caleydo.core.data.virtualarray.events;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;

/**
 * Event to signal that a {@link DimensionVirtualArray} has changed. For details see {@link VAUpdateEvent}.
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class DimensionVAUpdateEvent
	extends VAUpdateEvent {

	public DimensionVAUpdateEvent() {
		// TODO Auto-generated constructor stub
	}

	public DimensionVAUpdateEvent(String dataDomainID, String perspectiveID, Object sender) {
		setDataDomainID(dataDomainID);
		setPerspectiveID(perspectiveID);
		setSender(sender);
	}

}
