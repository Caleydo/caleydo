package org.caleydo.core.manager.event.view.storagebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.selection.delta.StorageVADelta;

/**
 * Event to signal that ??? FIXME description about the meaning of virtual array deltas Migration from
 * EEventType.VA_UPDATE
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class StorageVAUpdateEvent
	extends VirtualArrayUpdateEvent<StorageVADelta> {

}
