package org.caleydo.core.data.virtualarray.events;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;

/**
 * Event to signal that a dimension VA should be updated using a delta. See {@link VADeltaEvent} for details.
 * EventType.VA_UPDATE
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class DimensionVADeltaEvent
	extends VADeltaEvent<DimensionVADelta> {

}
