package org.caleydo.core.view.opengl.canvas.grouper.compositegraphic;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.view.opengl.canvas.grouper.GrouperRenderStyle;
import org.caleydo.core.view.opengl.canvas.grouper.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.canvas.grouper.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.canvas.grouper.draganddrop.IDropArea;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.DrawingStrategyManager;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.group.AGroupDrawingStrategyRectangular;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.group.EGroupDrawingStrategyType;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.group.GroupDrawingStrategyDragged;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.group.IGroupDrawingStrategy;

import com.sun.opengl.util.j2d.TextRenderer;

public class GroupRepresentation
	implements ICompositeGraphic, IDropArea {

	private ArrayList<ICompositeGraphic> alChildren;
	private ICompositeGraphic parent;
	private ArrayList<Float> alDropPositions;
	private Vec3f vecPosition;
	private Vec3f vecHierarchyPosition;
	private float fHeight;
	private float fWidth;
	private float fDraggingStartMouseCoordinateX;
	private float fDraggingStartMouseCoordinateY;
	private int iHierarchyLevel;
	private boolean bCollapsed;
	private ClusterNode clusterNode;
	private GrouperRenderStyle renderStyle;
	private IGroupDrawingStrategy drawingStrategy;
	private DrawingStrategyManager drawingStrategyManager;

	public GroupRepresentation(ClusterNode clusterNode, GrouperRenderStyle renderStyle,
		IGroupDrawingStrategy drawingStrategy, DrawingStrategyManager drawingStrategyManager) {
		alChildren = new ArrayList<ICompositeGraphic>();
		alDropPositions = new ArrayList<Float>();
		vecPosition = new Vec3f();
		bCollapsed = false;
		this.clusterNode = clusterNode;
		this.renderStyle = renderStyle;
		this.drawingStrategy = drawingStrategy;
		this.drawingStrategyManager = drawingStrategyManager;
	}

	@Override
	public void add(ICompositeGraphic graphic) {
		alChildren.add(graphic);
		graphic.setParent(this);
	}

	@Override
	public void delete(ICompositeGraphic graphic) {
		alChildren.remove(graphic);
		graphic.setParent(null);
	}

	@Override
	public void draw(GL gl, TextRenderer textRenderer) {
		drawingStrategy.draw(gl, this, textRenderer);
	}

	@Override
	public void handleDragging(GL gl, float fMouseCoordinateX, float fMouseCoordinateY) {

		GroupDrawingStrategyDragged drawingStrategyDragged =
			(GroupDrawingStrategyDragged) drawingStrategyManager
				.getGroupDrawingStrategy(EGroupDrawingStrategyType.DRAGGED);
		drawingStrategyDragged.drawDragged(gl, this, fMouseCoordinateX, fMouseCoordinateY, fDraggingStartMouseCoordinateX,
			fDraggingStartMouseCoordinateY);

	}

	@Override
	public void setDraggingStartPoint(float fMouseCoordinateX, float fMouseCoordinateY) {
		fDraggingStartMouseCoordinateX = fMouseCoordinateX;
		fDraggingStartMouseCoordinateY = fMouseCoordinateY;
	}

	@Override
	public void handleDragOver(GL gl, Set<IDraggable> setDraggables, float fMouseCoordinateX,
		float fMouseCoordinateY) {

		AGroupDrawingStrategyRectangular drawingStrategyRectangular = null;
		int iDropPositionIndex = -1;

		if (drawingStrategy instanceof AGroupDrawingStrategyRectangular) {
			drawingStrategyRectangular = (AGroupDrawingStrategyRectangular) drawingStrategy;
			iDropPositionIndex =
				getDropPositionIndex(gl, setDraggables, fMouseCoordinateX, fMouseCoordinateY,
					drawingStrategyRectangular);
			if (iDropPositionIndex == -1)
				return;
		}
		else {
			return;
		}

		drawingStrategyRectangular.drawDropPositionMarker(gl, this, iDropPositionIndex);
	}

	@Override
	public void handleDrop(GL gl, Set<IDraggable> setDraggables, float fMouseCoordinateX,
		float fMouseCoordinateY) {

		int iDropPositionIndex = -1;

		if (drawingStrategy instanceof AGroupDrawingStrategyRectangular) {
			iDropPositionIndex =
				getDropPositionIndex(gl, setDraggables, fMouseCoordinateX, fMouseCoordinateY,
					(AGroupDrawingStrategyRectangular) drawingStrategy);
			if (iDropPositionIndex == -1)
				return;
		}
		else {
			return;
		}

	}

	private int getDropPositionIndex(GL gl, Set<IDraggable> setDraggables, float fMouseCoordinateX,
		float fMouseCoordinateY, AGroupDrawingStrategyRectangular drawingStrategyRectangular) {

		for (IDraggable draggable : setDraggables) {
			if (draggable == this)
				return -1;
			if (draggable instanceof ICompositeGraphic) {
				if (hasParent((ICompositeGraphic) draggable))
					return -1;
			}
		}

		return drawingStrategyRectangular.getClosestDropPositionIndex(gl, this, setDraggables,
			fMouseCoordinateY);
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

	@Override
	public float getWidth() {
		return fWidth;
	}

	public void calculateDrawingParameters(GL gl, TextRenderer textRenderer) {
		drawingStrategy.calculateDrawingParameters(gl, textRenderer, this);
	}

	@Override
	public void calculateDimensions(GL gl, TextRenderer textRenderer) {

		drawingStrategy.calculateDimensions(gl, textRenderer, this);
	}

	@Override
	public void setToMaxWidth(float fWidth, float fChildWidthOffseth) {

		this.fWidth = fWidth;
		for (ICompositeGraphic child : alChildren) {
			child.setToMaxWidth(fWidth - fChildWidthOffseth, fChildWidthOffseth);
		}

	}

	public boolean isCollapsed() {
		return bCollapsed;
	}

	public void setCollapsed(boolean bCollapsed) {
		this.bCollapsed = bCollapsed;
	}

	@Override
	public void calculateHierarchyLevels(int iLevel) {
		iHierarchyLevel = iLevel;

		for (ICompositeGraphic child : alChildren) {
			child.calculateHierarchyLevels(iLevel + 1);
		}
	}

	public int getHierarchyLevel() {
		return iHierarchyLevel;
	}

	@Override
	public int getID() {
		return clusterNode.getClusterNr();
	}

	public ArrayList<ICompositeGraphic> getChildren() {
		return alChildren;
	}

	public ArrayList<Float> getDropPositions() {
		return alDropPositions;
	}

	public void setHeight(float fHeight) {
		this.fHeight = fHeight;
	}

	public void setWidth(float fWidth) {
		this.fWidth = fWidth;
	}

	public IGroupDrawingStrategy getDrawingStrategy() {
		return drawingStrategy;
	}

	public void setDrawingStrategy(IGroupDrawingStrategy drawingStrategy) {
		this.drawingStrategy = drawingStrategy;
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
	public ICompositeGraphic getParent() {
		return parent;
	}

	@Override
	public void setParent(ICompositeGraphic parent) {
		this.parent = parent;
	}

	@Override
	public void updateDrawingStrategies(SelectionManager selectionManager,
		DrawingStrategyManager drawingStrategyManager) {

		if (selectionManager.checkStatus(ESelectionType.MOUSE_OVER, getID())) {
			drawingStrategy =
				drawingStrategyManager.getGroupDrawingStrategy(EGroupDrawingStrategyType.MOUSE_OVER);

		}
		else if (selectionManager.checkStatus(ESelectionType.SELECTION, getID())) {
			drawingStrategy =
				drawingStrategyManager.getGroupDrawingStrategy(EGroupDrawingStrategyType.SELECTION);
		}
		else {
			drawingStrategy =
				drawingStrategyManager.getGroupDrawingStrategy(EGroupDrawingStrategyType.NORMAL);
		}

		for (ICompositeGraphic child : alChildren) {
			child.updateDrawingStrategies(selectionManager, drawingStrategyManager);
		}

	}

	@Override
	public Vec3f getHierarchyPosition() {
		return vecHierarchyPosition;
	}

	@Override
	public void setHierarchyPosition(Vec3f vecHierarchyPosition) {
		this.vecHierarchyPosition = vecHierarchyPosition;

		for (ICompositeGraphic child : alChildren) {
			child.setHierarchyPosition(vecHierarchyPosition);
		}
	}

	@Override
	public void setSelectionType(ESelectionType selectionType, SelectionManager selectionManager) {
		selectionManager.addToType(selectionType, getID());
		for (ICompositeGraphic child : alChildren) {
			child.setSelectionType(selectionType, selectionManager);
		}
	}

	@Override
	public void addAsDraggable(DragAndDropController dragAndDropController) {

		Set<IDraggable> setDraggables = dragAndDropController.getDraggables();

		for (IDraggable draggable : setDraggables) {
			if (hasParent(draggable))
				return;
		}

		dragAndDropController.addDraggable(this);
		for (ICompositeGraphic child : alChildren) {
			child.removeFromDraggables(dragAndDropController);
		}

	}

	@Override
	public String getName() {
		return clusterNode.getNodeName();
	}

	@Override
	public void removeFromDraggables(DragAndDropController dragAndDropController) {
		dragAndDropController.removeDraggable(this);
		for (ICompositeGraphic child : alChildren) {
			child.removeFromDraggables(dragAndDropController);
		}
	}
}
