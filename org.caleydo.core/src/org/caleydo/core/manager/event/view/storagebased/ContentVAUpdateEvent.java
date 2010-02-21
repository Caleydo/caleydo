package org.caleydo.core.manager.event.view.storagebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.selection.delta.ContentVADelta;

@XmlRootElement
@XmlType
public class ContentVAUpdateEvent
	extends VirtualArrayUpdateEvent<ContentVADelta> {

}
