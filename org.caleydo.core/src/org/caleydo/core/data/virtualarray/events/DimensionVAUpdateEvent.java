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


}
