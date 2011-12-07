package org.caleydo.view.visbricks.dimensiongroup;

import java.util.Set;
import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.PickingType;
import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * Renderer for spacings between bricks inside of a {@link DimensionGroup}. This
 * class is responsible for handling drops of bricks that shall be reordered.
 * 
 * @author Christian
 * 
 */
public class BrickSpacingRenderer
	extends LayoutRenderer
	implements IDropArea
{
	private DimensionGroup dimensionGroup;
	private int id;
	private boolean renderDropIndicator = false;

	public BrickSpacingRenderer(DimensionGroup dimensionGroup, int id)
	{
		// super(new float[] { 1, 0, 0, 1 });
		this.dimensionGroup = dimensionGroup;
		this.id = id;

		final GLVisBricks visBricks = dimensionGroup.getVisBricksView();

		visBricks.addIDPickingListener(new APickingListener()
		{
			@Override
			public void dragged(Pick pick)
			{
				DragAndDropController dragAndDropController = visBricks
						.getDragAndDropController();
				if (dragAndDropController.isDragging()
						&& dragAndDropController.getDraggingMode() != null
						&& dragAndDropController.getDraggingMode()
								.equals("BrickDrag"
										+ BrickSpacingRenderer.this.dimensionGroup.getID()))
				{
					dragAndDropController.setDropArea(BrickSpacingRenderer.this);
				}
			}

		}, PickingType.BRICK_SPACER.name() + dimensionGroup.getID(), id);
	}

	@Override
	public void render(GL2 gl)
	{

		float width = dimensionGroup.getGroupColumn().getSizeScaledX();
		GLVisBricks visBricks = dimensionGroup.getVisBricksView();
		gl.glPushName(visBricks.getPickingManager().getPickingID(visBricks.getID(),
				PickingType.BRICK_SPACER.name() + dimensionGroup.getID(), id));
		
		gl.glColor4f(1, 0, 0, 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(-width / 2.0f, 0, 0);
		gl.glVertex3f(width / 2.0f, 0, 0);
		gl.glVertex3f(width / 2.0f, y, 0);
		gl.glVertex3f(-width / 2.0f, y, 0);
		gl.glEnd();
		
		if (renderDropIndicator)
		{
			gl.glLineWidth(3);
			gl.glColor4f(0, 0, 0, 1);
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(-width / 2.0f, y / 2.0f, 0);
			gl.glVertex3f(width / 2.0f, y / 2.0f, 0);
			gl.glEnd();
		}
		gl.glPopName();
	}

	@Override
	public void handleDragOver(GL2 gl, Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY)
	{
		setRenderDropIndicator(true);
	}

	@Override
	public void handleDrop(GL2 gl, Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY, DragAndDropController dragAndDropController)
	{
		setRenderDropIndicator(false);
		for (IDraggable draggable : draggables)
		{
			GLBrick draggedBrick = (GLBrick) draggable;
			// TODO: Insert code to reorder bricks here
		}

	}

	public boolean isRenderDropIndicator()
	{
		return renderDropIndicator;
	}

	public void setRenderDropIndicator(boolean renderDropIndicator)
	{
		this.renderDropIndicator = renderDropIndicator;
	}

	@Override
	public void handleDropAreaReplaced()
	{
		setRenderDropIndicator(false);

	}

}
