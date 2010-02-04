package org.caleydo.view.grouper.compositegraphic;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.view.grouper.draganddrop.DragAndDropController;
import org.caleydo.view.grouper.draganddrop.IDraggable;
import org.caleydo.view.grouper.drawingstrategies.DrawingStrategyManager;

import com.sun.opengl.util.j2d.TextRenderer;

public interface ICompositeGraphic extends IDraggable {

	public void add(ICompositeGraphic graphic);

	public void delete(ICompositeGraphic graphic);

	public void draw(GL gl, TextRenderer textRenderer);

	public void calculateDimensions(GL gl, TextRenderer textRenderer);

	public void calculateHierarchyLevels(int iLevel);

	public Vec3f getPosition();

	public void setPosition(Vec3f vecPosition);

	public float getHeight();

	public float getWidth();

	public void setHeight(float fHeight);

	public void setWidth(float fWidth);

	public int getID();

	public String getName();

	public void setToMaxWidth(float fWidth, float fChildWidthOffset);

	public void setParent(ICompositeGraphic parent);

	public ICompositeGraphic getParent();

	public boolean hasParent(IDraggable parent);

	public void updateDrawingStrategies(SelectionManager selectionManager,
			DrawingStrategyManager drawingStrategyManager);

	public void setHierarchyPosition(Vec3f vecHierarchyPosition);

	public Vec3f getHierarchyPosition();

	public void setSelectionType(SelectionType selectionType,
			SelectionManager selectionManager);

	public void addAsDraggable(DragAndDropController dragAndDropController);

	public void removeFromDraggables(DragAndDropController dragAndDropController);

	public void getOrderedTopElementCompositeList(
			Set<ICompositeGraphic> setComposites,
			ArrayList<ICompositeGraphic> alComposites);

	public ICompositeGraphic getRoot();

	public ICompositeGraphic getShallowCopy();

	public void setChildrensParent(ICompositeGraphic parent);

	public void removeOnChildAbsence();

	public int getHierarchyLevel();

	public void replaceChild(ICompositeGraphic childToReplace,
			ICompositeGraphic newChild);

	public ICompositeGraphic createDeepCopyWithNewIDs(int[] iConsecutiveID);

	public void printTree();

}
