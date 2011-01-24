/**
 * 
 */
package org.caleydo.view.filterpipeline.representation;

import gleem.linalg.Vec2f;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.media.opengl.GL2;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.filterpipeline.FilterItem;
import org.caleydo.view.filterpipeline.renderstyle.FilterPipelineRenderStyle;

/**
 * Represents a filter which can be rendered and dragged arround
 * 
 * @author Thomas Geymayer
 *
 */
public class FilterRepresentation
	implements IDraggable, IRenderable, IDropArea
{
	protected FilterPipelineRenderStyle renderStyle;
	protected PickingManager pickingManager;
	protected int viewId;
	
	protected static final float Z_POS_BODY = 0.1f;
	protected static final float Z_POS_MARK = 0.6f;
	protected static final float Z_POS_TEXT = 0.7f;
	protected static final float Z_POS_DRAG = 0.8f;
	protected static final float Z_POS_DRAG_OVER = 0.9f;

	protected FilterItem<?> filter;
	protected int iPickingID = -1;
	protected SelectionType selectionType = SelectionType.NORMAL;
	
	protected Vec2f vPos = new Vec2f();
	protected Vec2f vSize = new Vec2f();
	protected Vec2f vDragStart = null;
	
	protected int mouseOverItem = -1;
	
	float heightLeft = 0;
	float heightRight = 0;
	
	public FilterRepresentation( FilterPipelineRenderStyle renderStyle,
								 PickingManager pickingManager,
								 int viewId )
	{
		this.renderStyle = renderStyle;
		this.pickingManager = pickingManager;
		this.viewId = viewId;
	}
	
	public void setFilter(FilterItem<?> filter)
	{
		this.filter = filter;
		this.iPickingID = filter.getPickingID();
	}
	
	public FilterItem<?> getFilter()
	{
		return filter;
	}
	
	public void setPosition(Vec2f filterPosition)
	{
		vPos = filterPosition.copy();
	}
	
	public Vec2f getPosition()
	{
		return vPos;
	}
	
	public void setSize(Vec2f filterSize)
	{
		vSize = filterSize.copy();
	}
	
	public Vec2f getSize()
	{
		return vSize;
	}
	
	public float getHeightLeft()
	{
		return vSize.y() * (filter.getInput().size()/100.f);
	}
	
	public float getHeightRight()
	{
		return vSize.y() * (filter.getOutput().size()/100.f);
	}

	@Override
	public void render(GL2 gl, CaleydoTextRenderer textRenderer)
	{
		heightLeft = getHeightLeft();
		heightRight = getHeightRight();

		// render filter
		gl.glPushName(iPickingID);
		renderShape
		(
			gl,
			GL2.GL_QUADS,
			renderStyle.getFilterColor(filter.getId()),
			Z_POS_BODY
		);
		gl.glPopName();
		
		// render selection/mouseover if needed
		if( selectionType != SelectionType.NORMAL )
		{
			gl.glLineWidth
			( 
				(selectionType == SelectionType.SELECTION)
					? SelectionType.SELECTION.getLineWidth()
	                : SelectionType.MOUSE_OVER.getLineWidth()
	        );
			
			renderShape
			(
				gl,
				GL2.GL_LINE_LOOP,
				(selectionType == SelectionType.SELECTION)
					? SelectionType.SELECTION.getColor()
					: SelectionType.MOUSE_OVER.getColor(),
				Z_POS_MARK
			);
		}
		
		// currently not filtered elements
		textRenderer.renderText
		(
			gl,
			""+filter.getOutput().size(),
			vPos.x() + vSize.x() - 0.4f,
			vPos.y() + heightRight + 0.05f,
			Z_POS_TEXT,
			0.007f,
			20
		);
		
		// label
		textRenderer.renderText
		(
			gl,
			(filter.getOutput().size() - filter.getInput().size())
			+ " (-"+filter.getSizeVADelta()+")",
			vPos.x() + 0.05f,
			vPos.y() + 0.05f,
			Z_POS_TEXT,
			0.007f,
			20
		);
	}
	
	protected void renderShape( GL2 gl,
			                    int renderMode,
			                    final Vec2f pos,
			                    float width,
			                    float heightLeft,
			                    float heightRight,
			                    float offsetRight,
			                    float[] color,
			                    float z )
	{
		gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT);
		gl.glBegin(renderMode);
		{
			gl.glColor4fv(color, 0);
	
			gl.glVertex3f(pos.x(), pos.y(), z);
			gl.glVertex3f(pos.x(), pos.y() + heightLeft, z);
			gl.glVertex3f(pos.x() + width, pos.y() + offsetRight + heightRight, z);
			gl.glVertex3f(pos.x() + width, pos.y() + offsetRight, z);			
		}
		gl.glEnd();
		gl.glPopAttrib();
	}
	
	protected void renderShape( GL2 gl,
            int renderMode,
            final Vec2f pos,
            float width,
            float heightLeft,
            float heightRight,
            float[] color,
            float z )
	{
		renderShape(gl, renderMode, pos, width, heightLeft, heightRight, 0, color, z);
	}
	
	protected void renderShape( GL2 gl,
            int renderMode,
            float[] color,
            float z )
	{
		renderShape(gl, renderMode, vPos, vSize.x(), heightLeft, heightRight, color, z);
	}
	
	/**
	 * Updates the selection state by gathering the selection state from the
	 * given {@link SelectionManager}
	 * 
	 * @param selectionManager
	 */
	public void updateSelections(SelectionManager selectionManager)
	{
		if( selectionManager.checkStatus(SelectionType.SELECTION, filter.getId()) )
			selectionType = SelectionType.SELECTION;
		else if( selectionManager.checkStatus(SelectionType.MOUSE_OVER, filter.getId()) )
			selectionType = SelectionType.MOUSE_OVER;
		else
			selectionType = SelectionType.NORMAL;
	}

	@Override
	public void setDraggingStartPoint(float mouseCoordinateX, float mouseCoordinateY)
	{
		vDragStart = new Vec2f(mouseCoordinateX, mouseCoordinateY);
	}

	@Override
	public void handleDragging(GL2 gl, float mouseCoordinateX, float mouseCoordinateY)
	{
		Vec2f tempPos = vPos.copy();
		vPos.add
		(
			new Vec2f
			(
				mouseCoordinateX - vDragStart.x(),
				mouseCoordinateY - vDragStart.y()
			)
		);

		renderShape
		(
			gl,
			GL2.GL_POLYGON,
			renderStyle.getFilterColorDrag(filter.getId()),
			Z_POS_DRAG
		);
		
		vPos = tempPos;
	}

	@Override
	public void handleDrop(GL2 gl, float mouseCoordinateX, float mouseCoordinateY)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void handleDragOver(GL2 gl, Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY)
	{
		gl.glLineWidth(5);
		renderShape
		(
			gl,
			GL2.GL_LINE_LOOP,
			renderStyle.DRAG_OVER_COLOR,
			Z_POS_DRAG_OVER
		);
	}

	@Override
	public void handleDrop(GL2 gl, Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY, DragAndDropController dragAndDropController)
	{
		// TODO Auto-generated method stub
	}

	public void handleIconMouseOver(int iExternalID)
	{
		mouseOverItem = iExternalID;
	}

	public void handleClearMouseOver()
	{
		mouseOverItem = -1;
	}
}
