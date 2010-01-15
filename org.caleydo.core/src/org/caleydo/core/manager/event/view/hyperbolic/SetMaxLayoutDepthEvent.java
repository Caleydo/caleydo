package org.caleydo.core.manager.event.view.hyperbolic;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.event.AEvent;

@XmlRootElement
@XmlType
public class SetMaxLayoutDepthEvent
	extends AEvent {

	private int iMaxLayoutDepth = 0;

	public int getMaxLayoutDepth() {
		return iMaxLayoutDepth;
	}

	public void setMaxLayoutDepth(int iMaxLayoutDepth) {
		this.iMaxLayoutDepth = iMaxLayoutDepth;
	}

	@Override
	public boolean checkIntegrity() {
		if (iMaxLayoutDepth <= 0)
			throw new IllegalStateException("iMaxLayoutDepth was not set");
		return true;
	}
}
