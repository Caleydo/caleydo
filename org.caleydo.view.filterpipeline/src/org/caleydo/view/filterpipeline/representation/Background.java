/**
 * 
 */
package org.caleydo.view.filterpipeline.representation;

import java.util.List;
import java.util.Set;
import javax.media.opengl.GL2;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.filterpipeline.FilterItem;
import org.caleydo.view.filterpipeline.renderstyle.FilterPipelineRenderStyle;

/**
 * @author Thomas Geymayer
 *
 */
public class Background
	implements IRenderable, IDropArea
{
	private FilterPipelineRenderStyle renderStyle;
	private int pickingId;
	
	private List<FilterItem<?>> filterList = null;
	private int firstFilterId = 0;
	private float[] dropPositions = null;
	
	/**
	 * 
	 * @param viewId
	 * @param pickingManager
	 * @param renderStyle
	 */
	public Background( int viewId,
			           PickingManager pickingManager,
			           FilterPipelineRenderStyle renderStyle )
	{
		this.renderStyle = renderStyle;
		
		pickingId =
			pickingManager.getPickingID
			(
				viewId,
				EPickingType.FILTERPIPE_BACKGROUND,
				0
			);
	}
	
	/**
	 * 
	 * @param filterList
	 */
	public void setFilterList(List<FilterItem<?>> filterList, int firstFilterId)
	{
		this.filterList = filterList;
		this.firstFilterId = firstFilterId;
		
		if( filterList.isEmpty() )
			return;
		
		dropPositions = new float[filterList.size() + 1];
		
		// before each filter
		int index = 0;
		for (FilterItem<?> filterItem : filterList)
		{
			dropPositions[index++] =
				filterItem.getRepresentation().getPosition().x();
		}
		
		// after last filter
		FilterRepresentation lastFilter =
			filterList.get(index - 1).getRepresentation();
		dropPositions[index] = lastFilter.getPosition().x()
							 + lastFilter.getSize().x();
	}

	@Override
	public void handleDragOver(GL2 gl, Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY)
	{
		int nearestDropPositionId =
			getClosestDropPositionIndex(draggables, mouseCoordinateX);
		
		if( nearestDropPositionId < 0 )
			return;
		
		gl.glLineWidth(renderStyle.DRAG_LINE_WIDTH);
		gl.glBegin(GL2.GL_LINES);
		{
			gl.glColor4fv(renderStyle.DRAG_LINE_COLOR, 0);
			gl.glVertex3f
			(
				dropPositions[nearestDropPositionId],
				renderStyle.FILTER_SPACING_BOTTOM,
				0.9f
			);
			gl.glVertex3f
			(
				dropPositions[nearestDropPositionId],
				renderStyle.getViewFrustum().getHeight()
				- renderStyle.FILTER_SPACING_TOP,
				0.9f
			);
		}
		gl.glEnd();
	}

	@Override
	public void handleDrop(GL2 gl, Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY, DragAndDropController dragAndDropController)
	{
		int dropId = getClosestDropPositionIndex(draggables, mouseCoordinateX);
		
		if( dropId < 0 )
			return;
		
		FilterRepresentation draggedFilter =
			getFilterRepresentation(draggables);
		
		int dragId = draggedFilter.getFilter().getId(),
			offset = dropId - dragId;
		
		if( dropId > dragId )
			offset -= 1;
		
		draggedFilter.getFilter().triggerMove(offset);
	}
	
	private int getClosestDropPositionIndex( Set<IDraggable> draggables,
											 float mouseCoordinateX )
	{
		FilterRepresentation draggedFilter =
			getFilterRepresentation(draggables);

		int nearestDropPositionId = -1;
		float minDist = Float.MAX_VALUE;
		
		for(int i = firstFilterId; i < dropPositions.length; ++i)
		{
			float dist = Math.abs(mouseCoordinateX - dropPositions[i]);
			
			if( dist < minDist )
			{
				minDist = dist;
				nearestDropPositionId = i;
			}
		}
		
		// no self dragging
		if(    draggedFilter.getFilter().getId() == nearestDropPositionId
			|| (draggedFilter.getFilter().getId() + 1) == nearestDropPositionId )
			return -1;
		
		return nearestDropPositionId;
	}
	
	private FilterRepresentation getFilterRepresentation(Set<IDraggable> draggables)
	{
		if( draggables.size() > 1 )
		{
			System.err.println("getClosestDropPositionIndex: More than one draggable?");
			return null;
		}
		
		if( filterList.isEmpty() )
			return null;
		
		IDraggable draggable = draggables.iterator().next();
		if( !(draggable instanceof FilterRepresentation) )
			return null;
		
		return (FilterRepresentation) draggable;
	}

	@Override
	public void render(GL2 gl, CaleydoTextRenderer textRenderer)
	{
		float height = renderStyle.getViewFrustum().getHeight(),
	    	  width  = renderStyle.getViewFrustum().getWidth();
	
		gl.glPushName(pickingId);		
		gl.glBegin(GL2.GL_POLYGON);
		{
			gl.glColor4fv(renderStyle.BACKGROUND_COLOR, 0);
		
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, height, 0);
			gl.glVertex3f(width, height, 0);
			gl.glVertex3f(width, 0, 0);
		}
		gl.glEnd();		
		gl.glPopName();	
	}
}
