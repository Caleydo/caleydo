package org.caleydo.core.manager.event.view.tablebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * This event specifies is triggered to init the Comboboxes for the Scatterplot
 * 
 * @author J�rgen Pillhofer
 */
@XmlRootElement
@XmlType
public class InitAxisComboEvent
	extends AEvent {

	private String[] AxisNames = null;

	public String[] getAxisNames() {
		return AxisNames;
	}

	public void setAxisNames(String[] sAxisNames) {
		this.AxisNames = sAxisNames;
	}

	@Override
	public boolean checkIntegrity() {
		if (AxisNames == null)
			throw new IllegalStateException("No valid Axis Names");
		return true;
	}

}
