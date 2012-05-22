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
package org.caleydo.core.event.view.radial;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;

/**
 * This event specifies the position (the selected value) of the (depth-) slider in the Toolbox of Radial
 * Layout.
 * 
 * @author Christian Partl
 */
@XmlRootElement
@XmlType
public class UpdateDepthSliderPositionEvent
	extends AEvent {

	private int iDepthSliderPosition = -1;

	public int getDepthSliderPosition() {
		return iDepthSliderPosition;
	}

	public void setDepthSliderPosition(int iDepthSliderPosition) {
		this.iDepthSliderPosition = iDepthSliderPosition;
	}

	@Override
	public boolean checkIntegrity() {
		if (iDepthSliderPosition == -1)
			throw new IllegalStateException("iDepthSliderPosition was not set");

		return true;
	}

}
