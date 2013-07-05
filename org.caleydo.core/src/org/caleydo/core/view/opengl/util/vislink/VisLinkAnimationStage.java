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
package org.caleydo.core.view.opengl.util.vislink;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

/**
 * The class VisLinkAnimationStage provides a structure to store connection lines which should be drawn in the
 * same animation stage. When animated line drawing is activated, there are several stages. Lines of one stage
 * are drawn at the same time, while lines of the next stage will be drawn when all lines of this stage are
 * finished. This allows more flexibility at creating animations. It could be intended to draw the lines of
 * specific animation-stages in different direction. To specify the direction of a stages lines, the variable
 * "drawLinesInReverseDirection" is used. When creating a VisLinkAnimationStage-object, one can set this
 * option.
 * 
 * @author Oliver Pimas
 */

public class VisLinkAnimationStage {

	ArrayList<ArrayList<Vec3f>> connectionLines;
	boolean drawLinesInReverseDirection;

	/**
	 * Constructor of class VisLinkAnimationStage. reverseDirection is set to false.
	 * 
	 * @param connectionLines
	 *            A list containing all connection lines for this stage.
	 */
	public VisLinkAnimationStage(ArrayList<ArrayList<Vec3f>> connectionLines) {
		this.connectionLines = connectionLines;
		this.drawLinesInReverseDirection = false;
	}

	/**
	 * Constructor of class VisLinkAnimationStage. reverseDirection is set to the given argument.
	 * 
	 * @param connectionLines
	 *            A list containing all connection lines for this stage.
	 * @param reverseDirection
	 *            Specifies the drawing.direction of the lines (for animated drawing).
	 */
	public VisLinkAnimationStage(ArrayList<ArrayList<Vec3f>> connectionLines, boolean reverseDirection) {
		this.connectionLines = connectionLines;
		this.drawLinesInReverseDirection = reverseDirection;
	}

	/**
	 * Constructor of class VisLinkAnimationStage. An empty List of lines is created and reverseDirection is
	 * set to the given argument.
	 * 
	 * @param connectionLines
	 *            A list containing all connection lines for this stage.
	 * @param reverseDirection
	 *            Specifies the drawing.direction of the lines (for animated drawing).
	 */
	public VisLinkAnimationStage() {
		this.connectionLines = new ArrayList<ArrayList<Vec3f>>();
		this.drawLinesInReverseDirection = false;
	}

	/**
	 * Constructor of class VisLinkAnimationStage. An empty List of lines is created and reverseDirection is
	 * set to the given argument.
	 * 
	 * @param connectionLines
	 *            A list containing all connection lines for this stage.
	 * @param reverseDirection
	 *            Specifies the drawing.direction of the lines (for animated drawing).
	 */
	public VisLinkAnimationStage(boolean reverseDirection) {
		this.connectionLines = new ArrayList<ArrayList<Vec3f>>();
		this.drawLinesInReverseDirection = reverseDirection;
	}

	/**
	 * Sets the option to draw the lines of the stage in reverse direction to the given boolean.
	 * 
	 * @param reverseDirection
	 *            True if the lines should be drawn in reverse direction, false otherwise.
	 */
	public void setReverseLineDrawingDirection(boolean reverseDirection) {
		this.drawLinesInReverseDirection = reverseDirection;
	}

	/**
	 * Returns true if the lines would currently be drawn in reverse direction, false otherwise.
	 * 
	 * @return True if the lines would currently be drawn in reverse direction, false otherwise.
	 */
	public boolean reverseLineDrawingDirection() {
		return this.drawLinesInReverseDirection;
	}

	/**
	 * Returns a list of all connection lines in this stage.
	 * 
	 * @return A list of all connection lines in this stage.
	 */
	public ArrayList<ArrayList<Vec3f>> connectionLines() {
		return this.connectionLines;
	}

	/**
	 * Adds a line to the current animation stage.
	 * 
	 * @param line
	 *            The line to add.
	 */
	public void addLine(ArrayList<Vec3f> line) {
		this.connectionLines.add(line);
	}

	/**
	 * Returns true if the stage contains lines, false otherwise.
	 * 
	 * @return True if the stage contains lines, false otherwise.
	 */
	public boolean containsLines() {
		return this.connectionLines.isEmpty();
	}

}
