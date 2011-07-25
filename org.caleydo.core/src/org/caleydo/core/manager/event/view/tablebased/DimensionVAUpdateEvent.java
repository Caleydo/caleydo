package org.caleydo.core.manager.event.view.tablebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;

/**
 * Event to signal that ??? FIXME description about the meaning of virtual array deltas Migration from
 * EventType.VA_UPDATE
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class DimensionVAUpdateEvent
	extends VirtualArrayUpdateEvent<DimensionVADelta> {

}
