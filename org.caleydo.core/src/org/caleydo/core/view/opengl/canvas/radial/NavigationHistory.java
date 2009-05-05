package org.caleydo.core.view.opengl.canvas.radial;

import java.util.ArrayList;

public class NavigationHistory {

	private ArrayList<HistoryEntry> alHistoryEntries;
	private GLRadialHierarchy radialHierarchy;
	private DrawingController drawingController;
	private int iCurrentEntryPosition;

	public NavigationHistory(GLRadialHierarchy radialHierarchy, DrawingController drawingController) {
		this.radialHierarchy = radialHierarchy;
		this.drawingController = drawingController;
		alHistoryEntries = new ArrayList<HistoryEntry>();
		iCurrentEntryPosition = -1;
	}

	public void addNewHistoryEntry(DrawingState drawingState, PartialDisc pdCurrentRootElement,
		PartialDisc pdCurrentSelectedElement, int iMaxDisplayedHierarchyDepth) {

		if (iCurrentEntryPosition < alHistoryEntries.size() - 1) {
			for (int i = alHistoryEntries.size() - 1; i > iCurrentEntryPosition; i--) {
				alHistoryEntries.remove(iCurrentEntryPosition + 1);
			}
		}

		iCurrentEntryPosition++;
		alHistoryEntries.add(new HistoryEntry(drawingState, pdCurrentRootElement, pdCurrentSelectedElement,
			iMaxDisplayedHierarchyDepth));
	}

	public void goBack() {

		if (iCurrentEntryPosition <= 0) {
			return;
		}
		iCurrentEntryPosition--;

		applyCurrentHistoryEntry();
	}

	public void goForth() {

		if ((iCurrentEntryPosition < 0) || (iCurrentEntryPosition >= alHistoryEntries.size() - 1)) {
			return;
		}
		iCurrentEntryPosition++;

		applyCurrentHistoryEntry();
	}

	private void applyCurrentHistoryEntry() {

		HistoryEntry heCurrentEntry = alHistoryEntries.get(iCurrentEntryPosition);
		PartialDisc pdRootElement = heCurrentEntry.getRootElement();
		PartialDisc pdSelectedElement = heCurrentEntry.getSelectedElement();
		PartialDisc pdCurrentMouseOverElement = radialHierarchy.getCurrentMouseOverElement();

		pdCurrentMouseOverElement.setPDDrawingStrategyChildren(DrawingStrategyManager.get()
			.getDrawingStrategy(DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW), 3);

		pdRootElement.setCurrentStartAngle(heCurrentEntry.getRootElementStartAngle());
		pdSelectedElement.setCurrentStartAngle(heCurrentEntry.getSelectedElementStartAngle());

		radialHierarchy.setCurrentRootElement(pdRootElement);
		radialHierarchy.setCurrentSelectedElement(pdSelectedElement);
		radialHierarchy.setCurrentMouseOverElement(pdSelectedElement);
		radialHierarchy.setMaxDisplayedHierarchyDepth(heCurrentEntry.getMaxDisplayedHierarchyDepth());
		drawingController.setDrawingState(heCurrentEntry.getDrawingState());

		radialHierarchy.setDisplayListDirty();
	}

	public void setDrawingController(DrawingController drawingController) {
		this.drawingController = drawingController;
	}

	public int getPos() {
		return iCurrentEntryPosition;
	}

	public int getSize() {
		return alHistoryEntries.size() - 1;
	}
}
