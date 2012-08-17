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
import java.util.Set;
import javax.media.opengl.GL2;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.view.grouper.drawingstrategies.DrawingStrategyManager;
import com.jogamp.opengl.util.awt.TextRenderer;

/**
 * Interface for graphic representations that are arranged hierarchically.
 * 
 * @author Christian
 * 
 */
public interface ICompositeGraphic extends IDraggable {

	/**
	 * Adds a child to the current composite.
	 * 
	 * @param graphic
	 *            Child to be added.
	 */
	public void add(ICompositeGraphic graphic);

	/**
	 * Removes a child from the current composite.
	 * 
	 * @param graphic
	 *            Child to be removed.
	 */
	public void delete(ICompositeGraphic graphic);

	/**
	 * Draws the current composite and its children.
	 * 
	 * @param gl
	 *            GL2 Context.
	 * @param textRenderer
	 *            TextRenderer.
	 */
	public void draw(GL2 gl, TextRenderer textRenderer);

	/**
	 * Calculates the dimensions of the composite and its children.
	 * 
	 * @param gl
	 *            GL2 Context.
	 * @param textRenderer
	 *            TextRenderer.
	 */
	public void calculateDimensions(GL2 gl, TextRenderer textRenderer);

	/**
	 * Calculates the hierarchy level of the current composite and its children.
	 * 
	 * @param iLevel
	 *            Hierarchy level the current composite should get.
	 */
	public void calculateHierarchyLevels(int iLevel);

	/**
	 * @return Position of the composite.
	 */
	public Vec3f getPosition();

	/**
	 * Sets the position of the current composite to the specified value.
	 * 
	 * @param vecPosition
	 *            Position the composite should be set to.
	 */
	public void setPosition(Vec3f vecPosition);

	/**
	 * @return Height of the composite.
	 */
	public float getHeight();

	/**
	 * @return Width of the composite.
	 */
	public float getWidth();

	/**
	 * Sets the height of the current composite to the specified value.
	 * 
	 * @param fHeight
	 *            Height the composite should be set to.
	 */
	public void setHeight(float fHeight);

	/**
	 * Sets the width of the current composite to the specified value.
	 * 
	 * @param fWidth
	 *            Width the composite should be set to.
	 */
	public void setWidth(float fWidth);

	/**
	 * @return ID of the composite.
	 */
	public int getID();

	/**
	 * @return Name of the composite.
	 */
	public String getName();

	/**
	 * Sets the Width of the current composite to the specified one. Its
	 * children will recursively be set to the width minus the offtable.
	 * 
	 * @param fWidth
	 *            Width the current composite should be set to.
	 * @param fChildWidthOffset
	 *            Offset which should be subtracted from the width in every
	 *            recursion step.
	 */
	public void setToMaxWidth(float fWidth, float fChildWidthOffset);

	/**
	 * Sets the parent of the composite.
	 * 
	 * @param parent
	 *            New parent of the composite.
	 */
	public void setParent(ICompositeGraphic parent);

	/**
	 * @return Parent of the composite.
	 */
	public ICompositeGraphic getParent();

	/**
	 * Returns whether the current composite has the specified parent on its
	 * parent path or not.
	 * 
	 * @param parent
	 *            Parent that shall be checked for.
	 * @return True, if the composite has the specified parent on its parent
	 *         path, false otherwise.
	 */
	public boolean hasParent(IDraggable parent);

	/**
	 * Updates the selection status of the composite according to the selection
	 * manager.
	 * 
	 * @param selectionManager
	 *            SelectionManager.
	 * @param drawingStrategyManager
	 *            DrawingStrategyManager.
	 */
	public void updateSelections(SelectionManager selectionManager,
			DrawingStrategyManager drawingStrategyManager);

	/**
	 * Sets the position of the whole composite hierarchy.
	 * 
	 * @param vecHierarchyPosition
	 *            Position of the composite hierarchy.
	 */
	public void setHierarchyPosition(Vec3f vecHierarchyPosition);

	/**
	 * @return Position of the whole composite hierarchy.
	 */
	public Vec3f getHierarchyPosition();

	/**
	 * Recursively sets the selection type of the composites in the selection
	 * manager to the specified one.
	 * 
	 * @param selectionType
	 *            Selection type the composite and its children should be set
	 *            to.
	 * @param selectionManager
	 *            SelectionManager.
	 */
	public void setSelectionTypeRec(SelectionType selectionType,
			SelectionManager selectionManager);

	/**
	 * Adds the current composite as draggable to the specified
	 * DragAndDropController if its parent is not already added. Removes its
	 * children from draggables.
	 * 
	 * @param dragAndDropController
	 *            DragAndDropController.
	 */
	public void addAsDraggable(DragAndDropController dragAndDropController);

	/**
	 * Removes the current composite and its children from the draggable list of
	 * the specified DragAndDropController.
	 * 
	 * @param dragAndDropController
	 *            DragAndDropController.
	 */
	public void removeFromDraggables(DragAndDropController dragAndDropController);

	/**
	 * Creates an ordered list of composites that correspond to the specified
	 * set of composites. The ordering is given by the appearance of the
	 * composites in the tree, hence their visual appearance from top to bottom
	 * of the grouper view.
	 * 
	 * @param setComposites
	 *            Composites an ordered list should be created for.
	 * @param alComposites
	 *            The ordered list of composites.
	 * @param topLevelElementsOnly
	 *            Specifies whether the list should only contain top level
	 *            composites, i.e. if the specified set of ids contains
	 *            composites with parent-child relation, this parameter
	 *            determines if only the parents or parents and children should
	 *            be added to the list.
	 */
	public void getOrderedCompositeList(Set<ICompositeGraphic> setComposites,
			ArrayList<ICompositeGraphic> alComposites, boolean topLevelElementsOnly);

	/**
	 * @return The root element of the composite hierarchy.
	 */
	public ICompositeGraphic getRoot();

	/**
	 * @return A shallow copy of the composite, children are not copied.
	 */
	public ICompositeGraphic getShallowCopy();

	/**
	 * Sets the parent of the current composite's children.
	 * 
	 * @param parent
	 *            New parent of the composite's children.
	 */
	public void setChildrensParent(ICompositeGraphic parent);

	/**
	 * Recursively removes the current composite and the composites on its
	 * parent path if they do not have any children.
	 */
	public void removeOnChildAbsence();

	/**
	 * @return Hierarchy level of the current composite.
	 */
	public int getHierarchyLevel();

	/**
	 * Replaces one child with another.
	 * 
	 * @param childToReplace
	 *            Child that should be replaced.
	 * @param newChild
	 *            New child that should replace the old one.
	 */
	public void replaceChild(ICompositeGraphic childToReplace, ICompositeGraphic newChild);

	/**
	 * Creates a deep copy of the current composite, also children are copied.
	 * 
	 * @param tree
	 *            Tree of cluster nodes.
	 * @param iConsecutiveID
	 *            ID that will be incremented consecutively for each new copy of
	 *            a composite.
	 * @return Copy of the composite.
	 */
	public ICompositeGraphic createDeepCopyWithNewIDs(ClusterTree tree,
			int[] iConsecutiveID);

	/**
	 * Helper function to print attributes of all composites in the hierarchy to
	 * the console.
	 */
	public void printTree();

	/**
	 * @return True, if the composite is a leaf, false otherwise.
	 */
	public boolean isLeaf();

}
