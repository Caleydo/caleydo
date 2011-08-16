package org.caleydo.core.manager.event.view.tablebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.virtualarray.delta.RecordVADelta;

@XmlRootElement
@XmlType
public class RecordVADeltaEvent
	extends VirtualArrayDeltaEvent<RecordVADelta> {

}
