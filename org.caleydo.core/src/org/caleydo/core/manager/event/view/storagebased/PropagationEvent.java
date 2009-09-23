package org.caleydo.core.manager.event.view.storagebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Event to signal the propagation of virtual array delta TODO description about the meaning of propagation.
 * FIXME should the class hierarchy between VirtualArrayUpdateEvent and this class be different? e.g.
 * inheritance from a common super class? Migration from EEventType.VA_UPDATE
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class PropagationEvent
	extends VirtualArrayUpdateEvent {

}
