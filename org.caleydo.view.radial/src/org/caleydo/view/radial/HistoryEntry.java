/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial;

/**
 * A history entry represents a state of the navigation in the radial hierarchy.
 * Such a state can be reconstructed by the drawing state, the current root
 * element, the current selected element, the start angle of the root element,
 * the start angle of the selected element and the maximum displayed hierarchy
 * depth. This class holds and saves all of these parameters.
 * 
 * @author Christian Partl
 */
public class HistoryEntry {

	private ADrawingState drawingState;
	private PartialDisc pdRootElement;
	private PartialDisc pdSelectedElement;
	private int iMaxDisplayedHierarchyDepth;
	private float fRootElementStartAngle;
	private float fSelectedElementStartAngle;

	/**
	 * Constructor.
	 * 
	 * @param drawingState
	 *            Drawing state for the history entry.
	 * @param pdCurrentRootElement
	 *            Current root element for the history entry.
	 * @param pdCurrentSelectedElement
	 *            Current selected element for the history entry.
	 * @param iMaxDisplayedHierarchyDepth
	 *            Current maximum hierarchy depth that can be displayed.
	 */
	public HistoryEntry(ADrawingState drawingState, PartialDisc pdRootElement,
			PartialDisc pdSelectedElement, int iMaxDisplayedHierarchyDepth) {
		this.drawingState = drawingState;
		this.pdRootElement = pdRootElement;
		this.pdSelectedElement = pdSelectedElement;
		this.iMaxDisplayedHierarchyDepth = iMaxDisplayedHierarchyDepth;
		fRootElementStartAngle = pdRootElement.getCurrentStartAngle();
		fSelectedElementStartAngle = pdSelectedElement.getCurrentStartAngle();
	}

	public ADrawingState getDrawingState() {
		return drawingState;
	}

	public void setDrawingState(ADrawingState drawingState) {
		this.drawingState = drawingState;
	}

	public PartialDisc getRootElement() {
		return pdRootElement;
	}

	public void setRootElement(PartialDisc pdRootElement) {
		this.pdRootElement = pdRootElement;
	}

	public PartialDisc getSelectedElement() {
		return pdSelectedElement;
	}

	public void setSelectedElement(PartialDisc pdSelectedElement) {
		this.pdSelectedElement = pdSelectedElement;
	}

	public int getMaxDisplayedHierarchyDepth() {
		return iMaxDisplayedHierarchyDepth;
	}

	public void setMaxDisplayedHierarchyDepth(int iMaxDisplayedHierarchyDepth) {
		this.iMaxDisplayedHierarchyDepth = iMaxDisplayedHierarchyDepth;
	}

	public float getRootElementStartAngle() {
		return fRootElementStartAngle;
	}

	public float getSelectedElementStartAngle() {
		return fSelectedElementStartAngle;
	}
}
