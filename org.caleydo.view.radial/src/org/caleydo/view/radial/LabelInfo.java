/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
