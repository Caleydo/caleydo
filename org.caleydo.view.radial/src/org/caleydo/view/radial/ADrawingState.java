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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.util.clusterer.EDrawingStateType;

/**
 * Abstract base class for all drawing states. Each concrete drawing state
 * displays the radial hierarchy and handles selections in a certain way.
 * 
 * @author Christian Partl
 */
@XmlType
@XmlSeeAlso({ DrawingStateDetailOutside.class, DrawingStateFullHierarchy.class,
		ADrawingStateAnimation.class })
public abstract class ADrawingState {

	/**
	 * DrawingController that holds the drawing states.
	 */
	protected DrawingController drawingController;
	/**
	 * GLRadialHierarchy instance that is used.
	 */
	protected GLRadialHierarchy radialHierarchy;
	/**
	 * NavigationHistory instance that shall be used.
	 */
	protected NavigationHistory navigationHistory;

	/**
	 * Constructor.
	 * 
	 * @param drawingController
	 *            DrawingController that holds the drawing states.
	 * @param radialHierarchy
	 *            GLRadialHierarchy instance that is used.
	 * @param navigationHistory
	 *            NavigationHistory instance that shall be used.
	 */
	public ADrawingState(DrawingController drawingController,
			GLRadialHierarchy radialHierarchy, NavigationHistory navigationHistory) {
		this.drawingController = drawingController;
		this.radialHierarchy = radialHierarchy;
		this.navigationHistory = navigationHistory;
	}

	/**
	 * Draws the radial hierarchy in a way specified by the concrete drawing
	 * state.
	 * 
	 * @param fXCenter
	 *            X coordinate of the hierarchy's center.
	 * @param fYCenter
	 *            Y coordinate of the hierarchy's center.
	 * @param gl
	 *            GL2 object that shall be used for drawing.
	 * @param glu
	 *            GLU object that shall be used for drawing.
	 */
	public abstract void draw(float fXCenter, float fYCenter, GL2 gl, GLU glu);

	/**
	 * Method for handling mouse over events.
	 * 
	 * @param pdMouseOver
	 *            Partial disc where the mouse over event occurred.
	 */
	public abstract void handleMouseOver(PartialDisc pdMouseOver);

	/**
	 * Method for handling selection events.
	 * 
	 * @param pdSelected
	 *            Partial disc that has been selected.
	 */
	public abstract void handleSelection(PartialDisc pdSelected);

	/**
	 * Method for handling alternative selection events.
	 * 
	 * @param pdSelected
	 *            Partial disc that has been selected alternatively.
	 */
	public abstract void handleAlternativeSelection(PartialDisc pdSelected);

	/**
	 * @return Element that is considered to be the selected element of the
	 *         concrete drawing state.
	 */
	public abstract PartialDisc getSelectedElement();

	/**
	 * Gets the drawing state type of the current instance.
	 */
	public abstract EDrawingStateType getType();
}
