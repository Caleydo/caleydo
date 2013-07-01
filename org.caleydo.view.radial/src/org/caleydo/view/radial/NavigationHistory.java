/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial;

import java.util.ArrayList;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.radial.event.UpdateDepthSliderPositionEvent;

/**
 * Represents the history of states that the user visited while browsing the
 * radial hierarchy.
 * 
 * @author Christian Partl
 */
public class NavigationHistory {

	private ArrayList<HistoryEntry> alHistoryEntries;
	private GLRadialHierarchy radialHierarchy;
	private DrawingController drawingController;
	private int iCurrentEntryPosition;

	/**
	 * Constructor.
	 * 
	 * @param radialHierarchy
	 *            GLRadialHierarchy instance that is used.
	 * @param drawingController
	 *            DrawingController that holds the drawing states.
	 */
	public NavigationHistory(GLRadialHierarchy radialHierarchy,
			DrawingController drawingController) {
		this.radialHierarchy = radialHierarchy;
		this.drawingController = drawingController;
		alHistoryEntries = new ArrayList<HistoryEntry>();
		iCurrentEntryPosition = -1;
	}

	/**
	 * Adds a new {@link HistoryEntry} with the specified parameters to the
	 * navigation history.
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
	public void addNewHistoryEntry(ADrawingState drawingState,
			PartialDisc pdCurrentRootElement, PartialDisc pdCurrentSelectedElement,
			int iMaxDisplayedHierarchyDepth) {

		if (iCurrentEntryPosition < alHistoryEntries.size() - 1) {
			for (int i = alHistoryEntries.size() - 1; i > iCurrentEntryPosition; i--) {
				alHistoryEntries.remove(iCurrentEntryPosition + 1);
			}
		}

		iCurrentEntryPosition++;
		alHistoryEntries.add(new HistoryEntry(drawingState, pdCurrentRootElement,
				pdCurrentSelectedElement, iMaxDisplayedHierarchyDepth));
	}

	/**
	 * Sets the previous history entry (if there is one) active.
	 */
	public void goBack() {

		if (iCurrentEntryPosition <= 0) {
			return;
		}

		iCurrentEntryPosition--;

		applyCurrentHistoryEntry();
	}

	/**
	 * Sets the next history entry (if there is one) active.
	 */
	public void goForth() {

		if ((iCurrentEntryPosition < 0)
				|| (iCurrentEntryPosition >= alHistoryEntries.size() - 1)) {
			return;
		}

		iCurrentEntryPosition++;

		applyCurrentHistoryEntry();
	}

	/**
	 * Extracts all information from the current history entry and sets the
	 * currently active drawing state, hierarchy etc. accordingly.
	 */
	private void applyCurrentHistoryEntry() {

		HistoryEntry heCurrentEntry = alHistoryEntries.get(iCurrentEntryPosition);
		PartialDisc pdRootElement = heCurrentEntry.getRootElement();
		PartialDisc pdSelectedElement = heCurrentEntry.getSelectedElement();

		pdRootElement.setCurrentStartAngle(heCurrentEntry.getRootElementStartAngle());
		pdSelectedElement.setCurrentStartAngle(heCurrentEntry
				.getSelectedElementStartAngle());

		radialHierarchy.setCurrentRootElement(pdRootElement);
		radialHierarchy.setCurrentSelectedElement(pdSelectedElement);

		radialHierarchy.setMaxDisplayedHierarchyDepth(heCurrentEntry
				.getMaxDisplayedHierarchyDepth());

		UpdateDepthSliderPositionEvent updateDepthSliderPositionEvent = new UpdateDepthSliderPositionEvent();
		updateDepthSliderPositionEvent.setSender(radialHierarchy);
		updateDepthSliderPositionEvent.setDepthSliderPosition(heCurrentEntry
				.getMaxDisplayedHierarchyDepth());
		GeneralManager.get().getEventPublisher()
				.triggerEvent(updateDepthSliderPositionEvent);

		drawingController.setDrawingState(heCurrentEntry.getDrawingState());

		radialHierarchy.setDisplayListDirty();

		radialHierarchy.setNewSelection(SelectionType.SELECTION, pdSelectedElement);
	}

	/**
	 * Sets the drawing controller that holds the drawing states.
	 * 
	 * @param drawingController
	 *            DrawingController that holds the drawing states.
	 */
	public void setDrawingController(DrawingController drawingController) {
		this.drawingController = drawingController;
	}

	/**
	 * @return The position of the current history entry in the navigation
	 *         history.
	 */
	public int getPosition() {
		return iCurrentEntryPosition;
	}

	/**
	 * @return The size (number of history entries) of the navigation history.
	 */
	public int getSize() {
		return alHistoryEntries.size() - 1;
	}

	/**
	 * Sets the maximum displayed hierarchy depth of the current history entry.
	 * Using this function no new history entry has to be created when the user
	 * just changes the value of the maximum displayed hierarchy depth.
	 * 
	 * @param iMaxDisplayedHierarchyDepth
	 *            New value for the maximum displayed hierarchy depth of the
	 *            current history entry.
	 */
	public void setCurrentMaxDisplayedHierarchyDepth(int iMaxDisplayedHierarchyDepth) {

		if (iCurrentEntryPosition < 0)
			return;
		HistoryEntry heCurrentEntry = alHistoryEntries.get(iCurrentEntryPosition);
		if (heCurrentEntry == null) {
			return;
		}

		heCurrentEntry.setMaxDisplayedHierarchyDepth(iMaxDisplayedHierarchyDepth);
	}

	/**
	 * Creates a new history entry with the specified parameters and replaces
	 * the current histroy entry with it.
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
	public void replaceCurrentHistoryEntry(ADrawingState drawingState,
			PartialDisc pdCurrentRootElement, PartialDisc pdCurrentSelectedElement,
			int iMaxDisplayedHierarchyDepth) {

		if (iCurrentEntryPosition < 0)
			return;
		HistoryEntry heCurrentEntry = alHistoryEntries.get(iCurrentEntryPosition);
		if (heCurrentEntry == null) {
			return;
		}

		heCurrentEntry.setDrawingState(drawingState);
		heCurrentEntry.setRootElement(pdCurrentRootElement);
		heCurrentEntry.setSelectedElement(pdCurrentSelectedElement);
		heCurrentEntry.setMaxDisplayedHierarchyDepth(iMaxDisplayedHierarchyDepth);

	}

	/**
	 * Resets the navigation history, it will be empty then.
	 */
	public void reset() {
		alHistoryEntries.clear();
		iCurrentEntryPosition = -1;
	}

	public HistoryEntry getCurrentHistoryEntry() {
		return alHistoryEntries.get(iCurrentEntryPosition);
	}
}
