/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
