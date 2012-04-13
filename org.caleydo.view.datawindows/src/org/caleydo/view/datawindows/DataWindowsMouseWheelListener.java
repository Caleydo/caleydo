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
package org.caleydo.view.datawindows;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class DataWindowsMouseWheelListener extends MouseAdapter implements
		MouseWheelListener {
	private GLHyperbolic hyperbolic;
	private float wheelFactor;
	private int numberOfScrollsToFullScreen;
	private int numberOfScrollsUntilFullScreen;

	public DataWindowsMouseWheelListener(GLHyperbolic master) {
		hyperbolic = master;
		wheelFactor = 0.2f;
		this.numberOfScrollsToFullScreen = 5;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {

		hyperbolic.diskZoomIntensity = hyperbolic.diskZoomIntensity
				+ event.getWheelRotation() * wheelFactor;
		hyperbolic.disk.zoomTree(hyperbolic.diskZoomIntensity);

		if (hyperbolic.diskZoomIntensity < 1) {
			this.numberOfScrollsUntilFullScreen = this.numberOfScrollsToFullScreen;
			if (hyperbolic.displayFullView = true) {
				hyperbolic.displayFullView = false;
			}
		}

		if (hyperbolic.diskZoomIntensity < -1) {
			hyperbolic.diskZoomIntensity = -1;
			if (hyperbolic.displayFullView = true) {
				hyperbolic.displayFullView = false;
			}
		}
		if (hyperbolic.diskZoomIntensity > 1) {
			hyperbolic.diskZoomIntensity = 1;
			this.numberOfScrollsUntilFullScreen--;

		}
		if (this.numberOfScrollsUntilFullScreen == 0) {
			hyperbolic.displayFullView = true;
		}

	}
}