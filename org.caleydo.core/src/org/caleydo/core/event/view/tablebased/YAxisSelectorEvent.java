package org.caleydo.core.event.view.tablebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.event.AEvent;

/**
 * This event is triggered by a change in the X-Axis-Combobox in te Scatterplot
 * 
 * @author J�rgen Pillhofer
 */
@XmlRootElement
@XmlType
public class YAxisSelectorEvent
	extends AEvent {

	private int SelectedAxisIndex = -1;

	public int getSelectedAxis() {
		return SelectedAxisIndex;
	}

	public void setSelectedAxis(int iSelectedAxisIndex) {
		this.SelectedAxisIndex = iSelectedAxisIndex;
	}

	@Override
	public boolean checkIntegrity() {
		if (SelectedAxisIndex == -1)
			throw new IllegalStateException("No valid Axis-Selection");
		return true;
	}

}
