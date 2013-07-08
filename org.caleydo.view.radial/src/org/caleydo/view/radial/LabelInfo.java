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

import java.util.ArrayList;

/**
 * This class holds all information that shall be displayed in a label and
 * additionally keeps important informations the @link {@link LabelManager}
 * requires for positioning and drawing the label properly.
 * 
 * @author Christian Partl
 */
public class LabelInfo {

	private ArrayList<LabelLine> alLines;
	private int iSegmentLabelDepth;
	private float fSegmentXCenter;
	private float fSegmentYCenter;
	private float fSegmentCenterRadius;

	/**
	 * Constructor.
	 * 
	 * @param fSegmentXCenter
	 *            X coordinate of the center of the disc segment (partial disc).
	 * @param fSegmentYCenter
	 *            Y coordinate of the center of the disc segment (partial disc).
	 * @param fSegmentCenterRadius
	 *            Center radius of the disc segment (partial disc).
	 * @param iSegmentLabelDepth
	 *            Specifies at which level of the disc segment subtree where
	 *            labels shall be displayed the current disc segment (partial
	 *            disc) is. I.e. it is the drawing strategy depth of a partial
	 *            disc where a {@link PDDrawingStrategyLabelDecorator} has been
	 *            used.
	 */
	public LabelInfo(float fSegmentXCenter, float fSegmentYCenter,
			float fSegmentCenterRadius, int iSegmentLabelDepth) {
		this.fSegmentXCenter = fSegmentXCenter;
		this.fSegmentYCenter = fSegmentYCenter;
		this.fSegmentCenterRadius = fSegmentCenterRadius;
		this.iSegmentLabelDepth = iSegmentLabelDepth;
		alLines = new ArrayList<LabelLine>();
	}

	/**
	 * @return The center radius of the disc segment (partial disc).
	 */
	public float getSegmentCenterRadius() {
		return fSegmentCenterRadius;
	}

	/**
	 * Sets the center radius of the disc segment (partial disc) to a specified
	 * value.
	 * 
	 * @param fSegmentCenterRadius
	 *            The value the center radius shall be set to.
	 */
	public void setSegmentCenterRadius(float fSegmentCenterRadius) {
		this.fSegmentCenterRadius = fSegmentCenterRadius;
	}

	/**
	 * @return The list of lines of the label.
	 */
	public ArrayList<LabelLine> getLines() {
		return alLines;
	}

	/**
	 * Adds a specified {@link LabelLine} at the end of the current list of
	 * label lines.
	 * 
	 * @param labelLine
	 *            The line that should be added to the label.
	 */
	public void addLine(LabelLine labelLine) {
		alLines.add(labelLine);
	}

	/**
	 * @return X coordinate of the center of the disc segment (partial disc).
	 */
	public float getSegmentXCenter() {
		return fSegmentXCenter;
	}

	/**
	 * Sets the X coordinate of the center of the disc segment (partial disc)
	 * for the label to the specified value.
	 * 
	 * @param fSegmentXCenter
	 *            The value the x coordinate shall be set to.
	 */
	public void setSegmentXCenter(float fSegmentXCenter) {
		this.fSegmentXCenter = fSegmentXCenter;
	}

	/**
	 * @return Y coordinate of the center of the disc segment (partial disc).
	 */
	public float getSegmentYCenter() {
		return fSegmentYCenter;
	}

	/**
	 * Sets the Y coordinate of the center of the disc segment (partial disc)
	 * for the label to the specified value.
	 * 
	 * @param fSegmentXCenter
	 *            The value the y coordinate shall be set to.
	 */
	public void setSegmentYCenter(float fSegmentYCenter) {
		this.fSegmentYCenter = fSegmentYCenter;
	}

	/**
	 * @return The level in the disc segment subtree where labels shall be
	 *         displayed the current disc segment (partial disc) has. I.e. it is
	 *         the drawing strategy depth of a partial disc where a
	 *         {@link PDDrawingStrategyLabelDecorator} has been used.
	 */
	public int getSegmentLabelDepth() {
		return iSegmentLabelDepth;
	}

	/**
	 * Sets the level of the current disc segment it has in the disc segment
	 * subtree where labels shall be displayed (i.e. the drawing strategy depth
	 * of a partial disc where a {@link PDDrawingStrategyLabelDecorator} has
	 * been used) to a specified value.
	 * 
	 * @param iSegmentLabelDepth
	 *            Value the segment label depth shall be set to.
	 */
	public void setSegmentLabelDepth(int iSegmentLabelDepth) {
		this.iSegmentLabelDepth = iSegmentLabelDepth;
	}

}
