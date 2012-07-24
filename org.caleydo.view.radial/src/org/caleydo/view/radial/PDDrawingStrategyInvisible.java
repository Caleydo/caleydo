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
package org.caleydo.view.radial;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import org.caleydo.core.util.clusterer.EPDDrawingStrategyType;
import org.caleydo.core.view.opengl.picking.PickingManager;

/**
 * PDDrawingStrategyInvisible does not draw anything.
 * 
 * @author Christian Partl
 */
public class PDDrawingStrategyInvisible extends APDDrawingStrategy {

	/**
	 * Constructor.
	 * 
	 * @param pickingManager
	 *            The picking manager that should handle the picking of the
	 *            drawn elements.
	 * @param viewID
	 *            ID of the view where the elements will be displayed. Needed
	 *            for picking.
	 */
	public PDDrawingStrategyInvisible(PickingManager pickingManager, int viewID) {
		super(pickingManager, viewID);
	}

	@Override
	public void drawFullCircle(GL2 gl, GLU glu, PartialDisc pdDiscToDraw) {
		// Don't draw anything

	}

	@Override
	public void drawPartialDisc(GL2 gl, GLU glu, PartialDisc pdDiscToDraw) {
		// Don't draw anything
	}

	@Override
	public EPDDrawingStrategyType getDrawingStrategyType() {
		return EPDDrawingStrategyType.INVISIBLE;
	}

	@Override
	public float[] getColor(PartialDisc disc) {
		return new float[] { 1.0f, 1.0f, 1.0f, 0.0f };
	}

}
