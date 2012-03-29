package org.caleydo.core.event.view.treemap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.event.AEvent;

/**
 * Event for switching labels on/off.
 * 
 * @author Michael Lafer
 */

@XmlRootElement
@XmlType
public class ToggleLabelEvent
	extends AEvent {

	private boolean bIsDrawLabel;

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	public void setDrawLabel(boolean flag) {
		bIsDrawLabel = flag;
	}

	public boolean isDrawLabel() {
		return bIsDrawLabel;
	}

}
