package org.caleydo.core.view.opengl.canvas.grouper;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.util.clusterer.ClusterNode;

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
	private float fRelDraggingPosX;
	private float fRelDraggingPosY;
	private int iHierarchyLevel;
	private boolean bCollapsed;
	private ClusterNode clusterNode;
	private GrouperRenderStyle renderStyle;
	private IGroupDrawingStrategy drawingStrategy;

	public GroupRepresentation(ClusterNode clusterNode, GrouperRenderStyle renderStyle,
		IGroupDrawingStrategy drawingStrategy) {
		alChildren = new ArrayList<ICompositeGraphic>();
		alDropPositions = new ArrayList<Float>();
		vecPosition = new Vec3f();
		bCollapsed = false;
		this.clusterNode = clusterNode;
		this.renderStyle = renderStyle;
		this.drawingStrategy = drawingStrategy;
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

		float fGroupColor[] = renderStyle.getGroupColorForLevel(iHierarchyLevel);

		gl.glColor4f(fGroupColor[0], fGroupColor[1], fGroupColor[2], 0.5f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fMouseCoordinateX + fRelDraggingPosX, fMouseCoordinateY + fRelDraggingPosY, 0.1f);
		gl.glVertex3f(fMouseCoordinateX + fRelDraggingPosX + fWidth, fMouseCoordinateY + fRelDraggingPosY,
			0.1f);
		gl.glVertex3f(fMouseCoordinateX + fRelDraggingPosX + fWidth, fMouseCoordinateY + fRelDraggingPosY
			- fHeight, 0.1f);
		gl.glVertex3f(fMouseCoordinateX + fRelDraggingPosX, fMouseCoordinateY + fRelDraggingPosY - fHeight,
			0.1f);
		gl.glEnd();

	}

	@Override
	public void setDraggingStartPoint(float fMouseCoordinateX, float fMouseCoordinateY) {
		fRelDraggingPosX = vecPosition.x() - fMouseCoordinateX;
		fRelDraggingPosY = vecPosition.y() - fMouseCoordinateY;
	}

	@Override
	public void handleDragOver(GL gl, Set<IDraggable> alDraggables, float fMouseCoordinateX,
		float fMouseCoordinateY) {

		for (IDraggable draggable : alDraggables) {
			if (draggable == this)
				return;
			if (draggable instanceof ICompositeGraphic) {
				if (hasParent((ICompositeGraphic) draggable))
					return;
			}
		}

		AGroupDrawingStrategyRectangular drawingStrategyRectangular;
		int iDropPositionIndex = 0;

		if (drawingStrategy instanceof AGroupDrawingStrategyRectangular) {
			drawingStrategyRectangular = (AGroupDrawingStrategyRectangular) drawingStrategy;
			iDropPositionIndex =
				drawingStrategyRectangular.getClosestDropPositionIndex(gl, this, alDraggables, fMouseCoordinateY);
			if (iDropPositionIndex == -1)
				return;
		}
		else {
			return;
		}

		drawingStrategyRectangular.drawDropPositionMarker(gl, this, iDropPositionIndex);
	}

	@Override
	public void handleDrop(Set<IDraggable> alDraggables, float fMouseCoordinateX,
		float fMouseCoordinateY) {
		// TODO Auto-generated method stub

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
	public boolean hasParent(ICompositeGraphic parent) {
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
		
		for(ICompositeGraphic child : alChildren) {
			child.setHierarchyPosition(vecHierarchyPosition);
		}
	}
	
	@Override
	public void setSelectionType(ESelectionType selectionType, SelectionManager selectionManager) {
		selectionManager.addToType(selectionType, getID());
		for(ICompositeGraphic child : alChildren) {
			child.setSelectionType(selectionType, selectionManager);
		}
	}

	@Override
	public void addAsDraggable(DragAndDropController dragAndDropController) {
		dragAndDropController.addDraggable(this);
		for(ICompositeGraphic child : alChildren) {
			child.addAsDraggable(dragAndDropController);
		}
		
	}

}
