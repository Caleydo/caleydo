package org.caleydo.core.view.opengl.canvas.grouper;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.SelectionManager;

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

	public int getID();

	public void setToMaxWidth(float fWidth, float fChildWidthOffset);

	public void setParent(ICompositeGraphic parent);

	public ICompositeGraphic getParent();

	public boolean hasParent(ICompositeGraphic parent);

	public void updateDrawingStrategies(SelectionManager selectionManager,
		DrawingStrategyManager drawingStrategyManager);

	public void setHierarchyPosition(Vec3f vecHierarchyPosition);

	public Vec3f getHierarchyPosition();

	public void setSelectionType(ESelectionType selectionType, SelectionManager selectionManager);

	public void addAsDraggable(DragAndDropController dragAndDropController);
}
