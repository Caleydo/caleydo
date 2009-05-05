package org.caleydo.core.view.opengl.canvas.radial;

public class HistoryEntry {

	private DrawingState drawingState;
	private PartialDisc pdRootElement;
	private PartialDisc pdSelectedElement;
	private int iMaxDisplayedHierarchyDepth;
	private float fRootElementStartAngle;
	private float fSelectedElementStartAngle;

	public HistoryEntry(DrawingState drawingState, PartialDisc pdRootElement, PartialDisc pdSelectedElement,
		int iMaxDisplayedHierarchyDepth) {
		this.drawingState = drawingState;
		this.pdRootElement = pdRootElement;
		this.pdSelectedElement = pdSelectedElement;
		this.iMaxDisplayedHierarchyDepth = iMaxDisplayedHierarchyDepth;
		fRootElementStartAngle = pdRootElement.getCurrentStartAngle();
		fSelectedElementStartAngle = pdSelectedElement.getCurrentStartAngle();
	}

	public DrawingState getDrawingState() {
		return drawingState;
	}

	public void setDrawingState(DrawingState drawingState) {
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
