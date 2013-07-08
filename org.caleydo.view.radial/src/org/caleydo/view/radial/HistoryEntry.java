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
