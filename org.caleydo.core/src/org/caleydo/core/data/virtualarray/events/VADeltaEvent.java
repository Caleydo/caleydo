/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.data.virtualarray.events;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.caleydo.core.event.AEvent;

/**
 * Event to signal that the virtual array has changed. It carries a {@link VirtualArrayDelta} as payload which
 * adapts the recipients virtual array for example by removing items.
 *
 * @author Alexander Lex
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class VADeltaEvent
	extends AEvent {

	/** delta between old and new selection */
	private VirtualArrayDelta virtualArrayDelta;

	/** additional information about the selection, e.g. to display in the info-box */
	private String info;

	public VirtualArrayDelta getVirtualArrayDelta() {
		return virtualArrayDelta;
	}

	public void setVirtualArrayDelta(VirtualArrayDelta virtualArrayDelta) {
		this.virtualArrayDelta = virtualArrayDelta;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public boolean checkIntegrity() {
		if (virtualArrayDelta == null) {
			throw new IllegalStateException("Integrity check in " + this
				+ "failed - virtualArrayDelta was null");
		}
		return true;
	}

}
