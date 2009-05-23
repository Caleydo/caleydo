package org.caleydo.core.view.opengl.canvas.radial;

import java.util.ArrayList;

import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.view.radial.UpdateDepthSliderPositionEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.radial.event.ClusterNodeSelectionEvent;

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
		
		radialHierarchy.setNewSelection(true);
		
		UpdateDepthSliderPositionEvent updateDepthSliderPositionEvent = new UpdateDepthSliderPositionEvent();
		updateDepthSliderPositionEvent.setSender(radialHierarchy);
		updateDepthSliderPositionEvent.setDepthSliderPosition(heCurrentEntry.getMaxDisplayedHierarchyDepth());
		GeneralManager.get().getEventPublisher().triggerEvent(updateDepthSliderPositionEvent);
		
		drawingController.setDrawingState(heCurrentEntry.getDrawingState());

		radialHierarchy.setDisplayListDirty();
		
		IEventPublisher eventPublisher = GeneralManager.get().getEventPublisher();
		ClusterNodeSelectionEvent event = new ClusterNodeSelectionEvent();
		event.setSender(this);
		event.setClusterNumber(pdSelectedElement.getElementID());
		event.setSelectionType(ESelectionType.SELECTION);

		eventPublisher.triggerEvent(event);
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

	public void setCurrentMaxDisplayedHierarchyDepth(int iMaxDisplayedHierarchyDepth) {

		if (iCurrentEntryPosition < 0)
			return;
		HistoryEntry heCurrentEntry = alHistoryEntries.get(iCurrentEntryPosition);
		if (heCurrentEntry == null) {
			return;
		}

		heCurrentEntry.setMaxDisplayedHierarchyDepth(iMaxDisplayedHierarchyDepth);
	}
	
	public void replaceCurrentHistoryEntry(DrawingState drawingState, PartialDisc pdCurrentRootElement,
		PartialDisc pdCurrentSelectedElement, int iMaxDisplayedHierarchyDepth) {

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
}
