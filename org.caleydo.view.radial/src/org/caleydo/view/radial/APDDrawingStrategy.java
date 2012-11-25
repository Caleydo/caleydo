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
 * APDDrawingStrategy is the abstract base class for all partial disc drawing
 * strategies. The drawing strategies determine the way partial discs are
 * displayed.
 * 
 * @author Christian Partl
 */
public abstract class APDDrawingStrategy {

	/**
	 * The number of slices which shall be drawn to approximate a full disc.
	 * Higher numbers produce a better approximation but the performance is
	 * worse.
	 */
	protected int iNumSlicesPerFullDisc;
	protected PickingManager pickingManager;
	protected int viewID;

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
	public APDDrawingStrategy(PickingManager pickingManager, int viewID) {
		this.pickingManager = pickingManager;
		this.viewID = viewID;
		iNumSlicesPerFullDisc = RadialHierarchyRenderStyle.NUM_SLICES_PER_FULL_DISC;
	}

	/**
	 * Draws a partial disc in a way determined by the concrete drawing strategy
	 * class.
	 * 
	 * @param gl
	 *            GL2 object that shall be used for drawing.
	 * @param glu
	 *            GLU object that shall be used for drawing.
	 * @param pdDiscToDraw
	 *            Partial disc that shall be drawn.
	 */
	public abstract void drawPartialDisc(GL2 gl, GLU glu, PartialDisc pdDiscToDraw);

	/**
	 * Draws a full circle in a way determined by the concrete drawing strategy
	 * class.
	 * 
	 * @param gl
	 *            GL2 object that shall be used for drawing.
	 * @param glu
	 *            GLU object that shall be used for drawing.
	 * @param pdDiscToDraw
	 *            Partial disc that shall be drawn.
	 */
	public abstract void drawFullCircle(GL2 gl, GLU glu, PartialDisc pdDiscToDraw);

	/**
	 * Gets the drawing strategy type of the current instance.
	 */
	public abstract EPDDrawingStrategyType getDrawingStrategyType();

	/**
	 * Gets the color the specified partial disc would be drawn with.
	 * 
	 * @param disc
	 *            Patrtial disc for which the color should be returned.
	 * @return Color of the specified partial disc as array of RGBA values.
	 */
	public abstract float[] getColor(PartialDisc disc);

	/**
	 * Gets the number of slices which shall be drawn to approximate a disc.
	 * 
	 * @return Number of slices per full disc.
	 */
	public int getNumSlicesPerFullDisc() {
		return iNumSlicesPerFullDisc;
	}

	/**
	 * Sets the number of slices which shall be drawn to approximate a disc.
	 * Higher numbers produce a better approximation but the performance is
	 * worse.
	 * 
	 * @param iNumSlicesPerFullDisc
	 *            Number of slices per full disc.
	 */
	public void setNumSlicesPerFullDisc(int iNumSlicesPerFullDisc) {
		this.iNumSlicesPerFullDisc = iNumSlicesPerFullDisc;
	}

	public PickingManager getPickingManager() {
		return pickingManager;
	}

	public void setPickingManager(PickingManager pickingManager) {
		this.pickingManager = pickingManager;
	}

	public int getViewID() {
		return viewID;
	}

	public void setViewID(int viewID) {
		this.viewID = viewID;
	}

}
