package org.caleydo.core.manager.event.view.dimensionbased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.virtualarray.delta.RecordVADelta;

@XmlRootElement
@XmlType
public class RecordVAUpdateEvent
	extends VirtualArrayUpdateEvent<RecordVADelta> {

}
