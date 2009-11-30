package org.caleydo.core.manager.event.view.storagebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

/**
 * This event specifies the Pointsize in the Scatterplot
 * 
 * @author Jürgen Pillhofer
 */
@XmlRootElement
@XmlType
public class SetPointSizeEvent
	extends AEvent {

	private int PointSize = -1;

	public int getPointSize() {
		return PointSize;
	}

	public void setPointSize(int iPointSize) {
		this.PointSize = iPointSize;
	}

	@Override
	public boolean checkIntegrity() {
		if (PointSize == -1)
			throw new IllegalStateException("Pointsize was not set");
		return true;
	}

}
