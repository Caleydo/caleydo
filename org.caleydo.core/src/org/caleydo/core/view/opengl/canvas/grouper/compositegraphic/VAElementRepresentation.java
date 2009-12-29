package org.caleydo.core.view.opengl.canvas.grouper.compositegraphic;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.view.opengl.canvas.grouper.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.canvas.grouper.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.DrawingStrategyManager;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.vaelement.EVAElementDrawingStrategyType;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.vaelement.IVAElementDrawingStrategy;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.vaelement.VAElementDrawingStrategyDragged;
import org.caleydo.core.view.opengl.util.AGLGUIElement;

import com.sun.opengl.util.j2d.TextRenderer;

public class VAElementRepresentation
	extends AGLGUIElement
	implements ICompositeGraphic {

	private Vec3f vecPosition;
	private Vec3f vecHierarchyPsoition;
	private float fDraggingStartMouseCoordinateX;
	private float fDraggingStartMouseCoordinateY;
	private float fHeight;
	private float fWidth;
	private int iVAIndex;
	private ICompositeGraphic parent;
	private ClusterNode clusterNode;
	private IVAElementDrawingStrategy drawingStrategy;
	private DrawingStrategyManager drawingStrategyManager;

	public VAElementRepresentation(ClusterNode clusterNode, IVAElementDrawingStrategy drawingStrategy,
		DrawingStrategyManager drawingStrategyManager) {
		vecPosition = new Vec3f();
		this.clusterNode = clusterNode;
		this.drawingStrategy = drawingStrategy;
		this.drawingStrategyManager = drawingStrategyManager;
	}

	@Override
	public void add(ICompositeGraphic graphic) {
		// This element is a leaf and therefore has no children.
	}

	@Override
	public void delete(ICompositeGraphic graphic) {
		// This element is a leaf and therefore has no children.
	}

	@Override
	public void draw(GL gl, TextRenderer textRenderer) {

		drawingStrategy.draw(gl, this, textRenderer);
	}

	@Override
	public Vec3f getPosition() {
		return vecPosition;
	}

	@Override
	public void setPosition(Vec3f vecPosition) {
		this.vecPosition = vecPosition;
	}

	@Override
	public float getHeight() {
		return fHeight;
	}

	public int getVAIndex() {
		return iVAIndex;
	}

	public void setVAIndex(int iVAIndex) {
		this.iVAIndex = iVAIndex;
	}

	@Override
	public float getWidth() {
		return fWidth;
	}

	@Override
	public void setToMaxWidth(float fWidth, float fChildWidthOffset) {
		if (this.fWidth < fWidth)
			this.fWidth = fWidth;
	}

	@Override
	public void calculateDimensions(GL gl, TextRenderer textRenderer) {
		drawingStrategy.calculateDimensions(gl, this, textRenderer);
	}

	@Override
	public void calculateHierarchyLevels(int iLevel) {
	}

	@Override
	public int getID() {
		return clusterNode.getClusterNr();
	}

	@Override
	public ICompositeGraphic getParent() {
		return parent;
	}

	@Override
	public void setParent(ICompositeGraphic parent) {
		this.parent = parent;
	}

	@Override
	public boolean hasParent(IDraggable parent) {
		if (this.parent == null)
			return false;
		if (this.parent == parent)
			return true;
		return this.parent.hasParent(parent);
	}

	@Override
	public String getName() {
		return clusterNode.getNodeName();
	}

	public IVAElementDrawingStrategy getDrawingStrategy() {
		return drawingStrategy;
	}

	public void setDrawingStrategy(IVAElementDrawingStrategy drawingStrategy) {
		this.drawingStrategy = drawingStrategy;
	}

	@Override
	public void updateDrawingStrategies(SelectionManager selectionManager,
		DrawingStrategyManager drawingStrategyManager) {

		if (selectionManager.checkStatus(ESelectionType.MOUSE_OVER, getID())) {
			drawingStrategy =
				drawingStrategyManager.getVAElementDrawingStrategy(EVAElementDrawingStrategyType.MOUSE_OVER);
		}
		else if (selectionManager.checkStatus(ESelectionType.SELECTION, getID())) {
			drawingStrategy =
				drawingStrategyManager.getVAElementDrawingStrategy(EVAElementDrawingStrategyType.SELECTION);
		}
		else {
			drawingStrategy =
				drawingStrategyManager.getVAElementDrawingStrategy(EVAElementDrawingStrategyType.NORMAL);
		}

	}

	@Override
	public Vec3f getHierarchyPosition() {
		return vecHierarchyPsoition;
	}

	@Override
	public void setHierarchyPosition(Vec3f vecHierarchyPosition) {
		this.vecHierarchyPsoition = vecHierarchyPosition;

	}

	@Override
	public void handleDragging(GL gl, float fMouseCoordinateX, float fMouseCoordinateY) {
		VAElementDrawingStrategyDragged drawingStrategyDragged =
			(VAElementDrawingStrategyDragged) drawingStrategyManager
				.getVAElementDrawingStrategy(EVAElementDrawingStrategyType.DRAGGED);

		drawingStrategyDragged.drawDragged(gl, this, fMouseCoordinateX, fMouseCoordinateY,
			fDraggingStartMouseCoordinateX, fDraggingStartMouseCoordinateY);

	}

	@Override
	public void setDraggingStartPoint(float fMouseCoordinateX, float fMouseCoordinateY) {
		fDraggingStartMouseCoordinateX = fMouseCoordinateX;
		fDraggingStartMouseCoordinateY = fMouseCoordinateY;
	}

	@Override
	public void setSelectionType(ESelectionType selectionType, SelectionManager selectionManager) {
		selectionManager.addToType(selectionType, getID());
	}

	@Override
	public void addAsDraggable(DragAndDropController dragAndDropController) {
		dragAndDropController.addDraggable(this);
	}

	@Override
	public void setHeight(float fHeight) {
		this.fHeight = fHeight;
	}

	@Override
	public void setWidth(float fWidth) {
		this.fWidth = fWidth;
	}

	@Override
	public void removeFromDraggables(DragAndDropController dragAndDropController) {
		dragAndDropController.removeDraggable(this);
	}
}
