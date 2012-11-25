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
package org.caleydo.view.grouper.compositegraphic;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.util.AGLGUIElement;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.view.grouper.GLGrouper;
import org.caleydo.view.grouper.GrouperRenderStyle;
import org.caleydo.view.grouper.drawingstrategies.DrawingStrategyManager;
import org.caleydo.view.grouper.drawingstrategies.group.AGroupDrawingStrategyRectangular;
import org.caleydo.view.grouper.drawingstrategies.group.EGroupDrawingStrategyType;
import org.caleydo.view.grouper.drawingstrategies.group.GroupDrawingStrategyDragged;
import org.caleydo.view.grouper.drawingstrategies.group.IGroupDrawingStrategy;

import com.jogamp.opengl.util.awt.TextRenderer;

public class GroupRepresentation implements ICompositeGraphic, IDropArea {

	private ArrayList<ICompositeGraphic> alChildren;
	private ICompositeGraphic parent;
	private ArrayList<Float> alDropPositions;
	private Vec3f vecPosition;
	private Vec3f vecHierarchyPosition;
	private Vec3f vecDraggingStartPosition;
	private float fHeight;
	private float fWidth;
	private float fDraggingStartMouseCoordinateX;
	private float fDraggingStartMouseCoordinateY;
	private int iHierarchyLevel;
	private boolean isCollapsed;
	private boolean isLeaf;
	private Set<SelectionType> selectionTypes;

	private ClusterNode clusterNode;
	private GrouperRenderStyle renderStyle;
	private IGroupDrawingStrategy drawingStrategy;
	private DrawingStrategyManager drawingStrategyManager;
	private GLGrouper glGrouper;

	public GroupRepresentation(ClusterNode clusterNode, GrouperRenderStyle renderStyle,
			IGroupDrawingStrategy drawingStrategy,
			DrawingStrategyManager drawingStrategyManager, GLGrouper glGrouper,
			boolean bLeaf) {
		alChildren = new ArrayList<ICompositeGraphic>();
		alDropPositions = new ArrayList<Float>();
		vecPosition = new Vec3f();
		isCollapsed = false;
		selectionTypes = new HashSet<SelectionType>();
		this.clusterNode = clusterNode;
		this.renderStyle = renderStyle;
		this.drawingStrategy = drawingStrategy;
		this.drawingStrategyManager = drawingStrategyManager;
		this.glGrouper = glGrouper;
		this.isLeaf = bLeaf;
	}

	@Override
	public void add(ICompositeGraphic graphic) {
		if (isLeaf)
			return;
		alChildren.add(graphic);
		graphic.setParent(this);
	}

	@Override
	public void delete(ICompositeGraphic graphic) {
		alChildren.remove(graphic);
	}

	@Override
	public void draw(GL2 gl, TextRenderer textRenderer) {
		if (isLeaf) {
			drawingStrategy.drawAsLeaf(gl, this, textRenderer);
		} else {
			drawingStrategy.draw(gl, this, textRenderer);
		}
	}

	@Override
	public void handleDragging(GL2 gl, float fMouseCoordinateX, float fMouseCoordinateY) {

		GroupDrawingStrategyDragged drawingStrategyDragged = (GroupDrawingStrategyDragged) drawingStrategyManager
				.getGroupDrawingStrategy(EGroupDrawingStrategyType.DRAGGED);
		if (isLeaf) {
			drawingStrategyDragged.drawDraggedLeaf(gl, this, fMouseCoordinateX,
					fMouseCoordinateY, fDraggingStartMouseCoordinateX,
					fDraggingStartMouseCoordinateY);
		} else {
			drawingStrategyDragged.drawDraggedGroup(gl, this, fMouseCoordinateX,
					fMouseCoordinateY, fDraggingStartMouseCoordinateX,
					fDraggingStartMouseCoordinateY);
		}

	}

	@Override
	public void setDraggingStartPoint(float fMouseCoordinateX, float fMouseCoordinateY) {
		fDraggingStartMouseCoordinateX = fMouseCoordinateX;
		fDraggingStartMouseCoordinateY = fMouseCoordinateY;
		vecDraggingStartPosition = new Vec3f(vecPosition);
	}

	@Override
	public void handleDragOver(GL2 gl, Set<IDraggable> setDraggables,
			float fMouseCoordinateX, float fMouseCoordinateY) {

		AGroupDrawingStrategyRectangular drawingStrategyRectangular = null;
		int iDropPositionIndex = getDropPositionIndex(gl, setDraggables,
				fMouseCoordinateX, fMouseCoordinateY);

		if (iDropPositionIndex == -1)
			return;

		if (drawingStrategy instanceof AGroupDrawingStrategyRectangular) {
			drawingStrategyRectangular = (AGroupDrawingStrategyRectangular) drawingStrategy;
		} else {
			return;
		}

		drawingStrategyRectangular.drawDropPositionMarker(gl, this, iDropPositionIndex);
	}

	@Override
	public void handleDrop(GL2 gl, Set<IDraggable> setDraggables,
			float fMouseCoordinateX, float fMouseCoordinateY,
			DragAndDropController dragAndDropController) {

		int iDropPositionIndex = getDropPositionIndex(gl, setDraggables,
				fMouseCoordinateX, fMouseCoordinateY);

		if (iDropPositionIndex == -1)
			return;

		Set<ICompositeGraphic> setComposites = new HashSet<ICompositeGraphic>();

		for (IDraggable draggable : setDraggables) {
			if (draggable instanceof ICompositeGraphic) {
				setComposites.add((ICompositeGraphic) draggable);
			}
		}

		ArrayList<ICompositeGraphic> alCompositesToInsert = new ArrayList<ICompositeGraphic>();

		ICompositeGraphic root = getRoot();
		root.getOrderedCompositeList(setComposites, alCompositesToInsert, true);

		for (int i = alCompositesToInsert.size() - 1; i >= 0; i--) {
			ICompositeGraphic composite = alCompositesToInsert.get(i);
			ICompositeGraphic copy = composite.getShallowCopy();

			if (iDropPositionIndex == alChildren.size()) {
				alChildren.add(copy);
			} else {
				alChildren.add(iDropPositionIndex, copy);
			}
			copy.setParent(this);

			dragAndDropController.removeDraggable(composite);
			dragAndDropController.addDraggable(copy);

			copy.setChildrensParent(copy);
		}

		for (ICompositeGraphic composite : alCompositesToInsert) {
			ICompositeGraphic parent = composite.getParent();
			if (parent != null) {
				parent.delete(composite);
				parent.removeOnChildAbsence();
			}
		}

		glGrouper.setHierarchyChanged(true);
		glGrouper.updateClusterTreeAccordingToGroupHierarchy();
		glGrouper.setDisplayListDirty();
	}

	/**
	 * Returns the index of the drop position that is as close as possible to
	 * the specified coordinates for the set of draggables.
	 * 
	 * @param gl
	 *            GL2 context.
	 * @param setDraggables
	 *            Draggables that should be droppable at the drop position with
	 *            the index returned by this method.
	 * @param fMouseCoordinateX
	 *            X coordinate of the mouse in world space.
	 * @param fMouseCoordinateY
	 *            Y coordinate of the mouse in world space.
	 * @return Drop position index for the draggables, -1 if no drop position
	 *         was found or is suitable.
	 */
	public int getDropPositionIndex(GL2 gl, Set<IDraggable> setDraggables,
			float fMouseCoordinateX, float fMouseCoordinateY) {

		AGroupDrawingStrategyRectangular drawingStrategyRectangular;

		if (drawingStrategy instanceof AGroupDrawingStrategyRectangular) {
			drawingStrategyRectangular = (AGroupDrawingStrategyRectangular) drawingStrategy;
		} else {
			return -1;
		}

		for (IDraggable draggable : setDraggables) {
			if (draggable == this)
				return -1;
			if (draggable instanceof ICompositeGraphic) {
				if (hasParent(draggable))
					return -1;
			}
		}

		return drawingStrategyRectangular.getClosestDropPositionIndex(gl, this,
				setDraggables, fMouseCoordinateY);
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

	/**
	 * Gets the scaled height of the GroupRepresentation.
	 * 
	 * @param viewportHeight
	 *            Width of the viewport.
	 * @return Scaled height of the GroupRepresentation.
	 */
	public float getScaledHeight(int viewportHeight) {
		if (drawingStrategy instanceof AGLGUIElement)
			return ((AGLGUIElement) drawingStrategy).getScaledSizeOf(viewportHeight,
					fHeight);
		return fHeight;
	}

	/**
	 * Gets the scaled width of the GroupRepresentation.
	 * 
	 * @param viewportWidth
	 *            Width of the viewport.
	 * @return Scaled width of the GroupRepresentation.
	 */
	public float getScaledWidth(int viewportWidth) {
		if (drawingStrategy instanceof AGLGUIElement)
			return ((AGLGUIElement) drawingStrategy).getScaledSizeOf(viewportWidth,
					fWidth);
		return fWidth;
	}

	@Override
	public float getWidth() {
		return fWidth;
	}

	/**
	 * Calculates parameters that are necessary for drawing.
	 * 
	 * @param gl
	 *            GL2 Context.
	 * @param textRenderer
	 *            TextRenderer.
	 */
	public void calculateDrawingParameters(GL2 gl, TextRenderer textRenderer) {
		drawingStrategy.calculateDrawingParameters(gl, textRenderer, this);
	}

	@Override
	public void calculateDimensions(GL2 gl, TextRenderer textRenderer) {
		if (isLeaf) {
			drawingStrategy.calculateDimensionsOfLeaf(gl, textRenderer, this);
		} else {
			drawingStrategy.calculateDimensions(gl, textRenderer, this);
		}
	}

	@Override
	public void setToMaxWidth(float fWidth, float fChildWidthOffset) {

		this.fWidth = fWidth;
		for (ICompositeGraphic child : alChildren) {
			child.setToMaxWidth(fWidth - fChildWidthOffset, fChildWidthOffset);
		}

	}

	/**
	 * @return True, if the group is collapsed, false otherwise.
	 */
	public boolean isCollapsed() {
		return isCollapsed;
	}

	/**
	 * @param bCollapsed
	 *            Determines whether the group shall be collapsed or not.
	 */
	public void setCollapsed(boolean bCollapsed) {
		this.isCollapsed = bCollapsed;
		glGrouper.setHierarchyChanged(true);
		glGrouper.setDisplayListDirty();
	}

	@Override
	public void calculateHierarchyLevels(int iLevel) {
		iHierarchyLevel = iLevel;

		for (ICompositeGraphic child : alChildren) {
			child.calculateHierarchyLevels(iLevel + 1);
		}
	}

	@Override
	public int getHierarchyLevel() {
		return iHierarchyLevel;
	}

	@Override
	public int getID() {
		return clusterNode.getID();
	}

	public ArrayList<ICompositeGraphic> getChildren() {
		return alChildren;
	}

	/**
	 * @return List of Y coordinates of drop positions.
	 */
	public ArrayList<Float> getDropPositions() {
		return alDropPositions;
	}

	@Override
	public void setHeight(float fHeight) {
		this.fHeight = fHeight;
	}

	@Override
	public void setWidth(float fWidth) {
		this.fWidth = fWidth;
	}

	/**
	 * @return Current drawing strategy of the group.
	 */
	public IGroupDrawingStrategy getDrawingStrategy() {
		return drawingStrategy;
	}

	/**
	 * Sets the drawing strategy of the current group.
	 * 
	 * @param drawingStrategy
	 *            Drawing strategy the group should use.
	 */
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
	public void updateSelections(SelectionManager selectionManager,
			DrawingStrategyManager drawingStrategyManager) {

		Collection<SelectionType> selectionTypes = selectionManager
				.getSelectionTypes(getID());
		this.selectionTypes.clear();
		if (selectionTypes != null) {
			this.selectionTypes.addAll(selectionTypes);

			if (selectionManager.checkStatus(SelectionType.MOUSE_OVER, getID())) {
				drawingStrategy = drawingStrategyManager
						.getGroupDrawingStrategy(EGroupDrawingStrategyType.MOUSE_OVER);
			} else if (selectionManager.checkStatus(SelectionType.SELECTION, getID())) {
				drawingStrategy = drawingStrategyManager
						.getGroupDrawingStrategy(EGroupDrawingStrategyType.SELECTION);
			} else {
				drawingStrategy = drawingStrategyManager
						.getGroupDrawingStrategy(EGroupDrawingStrategyType.NORMAL);
			}
		} else {
			drawingStrategy = drawingStrategyManager
					.getGroupDrawingStrategy(EGroupDrawingStrategyType.NORMAL);
		}

		for (ICompositeGraphic child : alChildren) {
			child.updateSelections(selectionManager, drawingStrategyManager);
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
	public void setSelectionTypeRec(SelectionType selectionType,
			SelectionManager selectionManager) {
		if (selectionType != SelectionType.NORMAL)
			selectionManager.addToType(selectionType, getID());
		clusterNode.setSelectionType(selectionType);
		for (ICompositeGraphic child : alChildren) {
			child.setSelectionTypeRec(selectionType, selectionManager);
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
		return clusterNode.getLabel();
	}

	@Override
	public void removeFromDraggables(DragAndDropController dragAndDropController) {
		dragAndDropController.removeDraggable(this);
		for (ICompositeGraphic child : alChildren) {
			child.removeFromDraggables(dragAndDropController);
		}
	}

	// @Override
	// public void getOrderedTopElementCompositeList(
	// Set<ICompositeGraphic> setComposites,
	// ArrayList<ICompositeGraphic> alComposites) {
	//
	// if (alComposites == null || setComposites == null)
	// return;
	//
	// for (ICompositeGraphic child : alChildren) {
	//
	// boolean bChildInList = false;
	//
	// for (ICompositeGraphic composite : setComposites) {
	// if (child == composite) {
	// alComposites.add(child);
	// bChildInList = true;
	// break;
	// }
	// }
	//
	// if (!bChildInList)
	// child.getOrderedTopElementCompositeList(setComposites,
	// alComposites);
	// }
	//
	// }

	@Override
	public void getOrderedCompositeList(Set<ICompositeGraphic> setComposites,
			ArrayList<ICompositeGraphic> alComposites, boolean topLevelElementsOnly) {

		if (alComposites == null || setComposites == null)
			return;

		for (ICompositeGraphic composite : setComposites) {
			if (this == composite) {
				alComposites.add(this);
				if (topLevelElementsOnly)
					return;
			}
		}

		for (ICompositeGraphic child : alChildren) {
			child.getOrderedCompositeList(setComposites, alComposites,
					topLevelElementsOnly);
		}

	}

	@Override
	public ICompositeGraphic getRoot() {
		if (parent == null)
			return this;
		return parent.getRoot();
	}

	@Override
	public ICompositeGraphic getShallowCopy() {
		GroupRepresentation copy = new GroupRepresentation(clusterNode, renderStyle,
				drawingStrategy, drawingStrategyManager, glGrouper, isLeaf);
		copy.alChildren = alChildren;
		copy.vecHierarchyPosition = vecHierarchyPosition;
		copy.vecPosition = vecPosition;
		copy.fDraggingStartMouseCoordinateX = fDraggingStartMouseCoordinateX;
		copy.fDraggingStartMouseCoordinateY = fDraggingStartMouseCoordinateY;
		copy.fHeight = fHeight;
		copy.fWidth = fWidth;
		copy.alDropPositions = alDropPositions;
		copy.isCollapsed = isCollapsed;
		copy.iHierarchyLevel = iHierarchyLevel;
		copy.parent = parent;
		copy.addSelectionTypes(selectionTypes);

		glGrouper.addGroupRepresentation(copy.getID(), copy);

		return copy;
	}

	@Override
	public void setChildrensParent(ICompositeGraphic parent) {
		for (ICompositeGraphic child : alChildren) {
			child.setParent(parent);
		}

	}

	@Override
	public void removeOnChildAbsence() {
		if (alChildren.size() <= 0) {
			glGrouper.removeGroupRepresentation(getID());
			if (parent != null) {
				parent.delete(this);
				parent.removeOnChildAbsence();
			}
		}
	}

	@Override
	public void replaceChild(ICompositeGraphic childToReplace, ICompositeGraphic newChild) {
		int iChildIndex = alChildren.indexOf(childToReplace);

		if (iChildIndex == -1)
			return;

		alChildren.add(iChildIndex, newChild);
		alChildren.remove(childToReplace);
	}

	@Override
	public ICompositeGraphic createDeepCopyWithNewIDs(ClusterTree tree,
			int[] iConsecutiveID) {

		ClusterNode copiedNode = null;
		if (isLeaf()) {
			copiedNode = new ClusterNode(tree, clusterNode.getLabel(), iConsecutiveID[0],
					false, clusterNode.getLeafID());
		} else {
			copiedNode = new ClusterNode(tree, clusterNode.getLabel() + "_copy",
					iConsecutiveID[0], false, clusterNode.getLeafID());
		}
		GroupRepresentation copy = new GroupRepresentation(copiedNode, renderStyle,
				drawingStrategy, drawingStrategyManager, glGrouper, isLeaf);
		copy.addSelectionTypes(selectionTypes);
		for (ICompositeGraphic child : alChildren) {
			iConsecutiveID[0]++;
			ICompositeGraphic copiedChild = child.createDeepCopyWithNewIDs(tree,
					iConsecutiveID);
			copy.add(copiedChild);
		}

		copy.vecHierarchyPosition = vecHierarchyPosition;
		copy.vecPosition = vecPosition;
		copy.fDraggingStartMouseCoordinateX = fDraggingStartMouseCoordinateX;
		copy.fDraggingStartMouseCoordinateY = fDraggingStartMouseCoordinateY;
		copy.fHeight = fHeight;
		copy.fWidth = fWidth;
		copy.alDropPositions.addAll(alDropPositions);
		copy.isCollapsed = isCollapsed;
		copy.iHierarchyLevel = iHierarchyLevel;
		copy.parent = parent;

		glGrouper.addGroupRepresentation(copy.getID(), copy);
		glGrouper.addNewSelectionID(copy.getID());

		return copy;

	}

	@Override
	public boolean isLeaf() {
		return isLeaf;
	}

	/**
	 * @param bLeaf
	 *            Determines whether the group should be treated as leaf
	 *            composite or not.
	 */
	public void setLeaf(boolean bLeaf) {
		this.isLeaf = bLeaf;
	}

	/**
	 * @return Gets the position where dragging of the group started.
	 */
	public Vec3f getDraggingStartPosition() {
		return vecDraggingStartPosition;
	}

	@Override
	public void printTree() {
		StringBuffer children = new StringBuffer();

		for (ICompositeGraphic child : alChildren) {
			children.append("ID: " + child.getID());
			children.append(", Name: " + child.getName() + "; ");
		}

		System.out.println("ID: " + getID() + ", Name: " + getName() + ", Parent: "
				+ ((parent != null) ? parent.getID() : "null") + ", NumChildren: "
				+ alChildren.size() + ", NumDropPositions: " + alDropPositions.size());
		System.out.println("Children: " + children.toString() + "\n");

		if (!isLeaf && alDropPositions.size() != alChildren.size() + 1)
			System.out.println("ALERT!!!!!!!!!!!!!!!!!!!!!!");

		for (ICompositeGraphic child : alChildren) {
			child.printTree();
		}
	}

	/**
	 * @return ClusterNode that corresponds to the GroupRepresentation.
	 */
	public ClusterNode getClusterNode() {
		return clusterNode;
	}

	/**
	 * @return All selection types of the group.
	 */
	public Set<SelectionType> getSelectionTypes() {
		return selectionTypes;
	}

	/**
	 * Adds the specified selection types to the group.
	 * 
	 * @param selectionTypes
	 *            SelectionTypes that should be added.
	 */
	private void addSelectionTypes(Set<SelectionType> selectionTypes) {
		this.selectionTypes.addAll(selectionTypes);
	}

	@Override
	public void handleDrop(GL2 gl, float mouseCoordinateX, float mouseCoordinateY) {
	}

	@Override
	public void handleDropAreaReplaced()
	{
		// TODO Auto-generated method stub
		
	}
}
