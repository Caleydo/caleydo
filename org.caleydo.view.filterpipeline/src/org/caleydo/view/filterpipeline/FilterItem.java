package org.caleydo.view.filterpipeline;

import java.util.Set;
import javax.media.opengl.GL2;
import org.caleydo.core.data.filter.ContentFilter;
import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.data.filter.StorageFilter;
import org.caleydo.core.data.filter.event.MoveContentFilterEvent;
import org.caleydo.core.data.filter.event.MoveFilterEvent;
import org.caleydo.core.data.filter.event.MoveStorageFilterEvent;
import org.caleydo.core.data.filter.event.RemoveContentFilterEvent;
import org.caleydo.core.data.filter.event.RemoveFilterEvent;
import org.caleydo.core.data.filter.event.RemoveStorageFilterEvent;
import org.caleydo.core.data.virtualarray.IVirtualArray;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.filterpipeline.representation.FilterRepresentation;
import org.caleydo.view.filterpipeline.representation.IRenderable;

/**
 * @brief Represents a filter in the GLFilterPipeline view
 * 
 * Also renders a context menu if needed
 * 
 * @author Thomas Geymayer
 *
 */
public class FilterItem<DeltaType extends VirtualArrayDelta<?>>
    implements IRenderable, IDropArea
{
	private int id;
	private int pickingId;
	private Filter<DeltaType> filter;
	private FilterRepresentation representation = null;

	IVirtualArray<?,DeltaType,?> input = null;
	IVirtualArray<?,DeltaType,?> output = null;
	
	/**
	 * Constructor
	 * 
	 * @param id
	 * @param filter
	 * @param pickingManager
	 * @param iUniqueID 
	 */
	public FilterItem( int id,
					   Filter<DeltaType> filter,
					   PickingManager pickingManager,
					   int iUniqueID )
	{
		this.id = id;
		pickingId = pickingManager.getPickingID
		(
			iUniqueID,
			EPickingType.FILTERPIPE_FILTER,
			id
		);
		this.filter = filter;
	}
	
	@Override
	public void render(GL2 gl, CaleydoTextRenderer textRenderer)
	{
		representation.render(gl, textRenderer);
	}
	
	/**
	 * Set the filter representation
	 * 
	 * @param representation
	 */
	public void setRepresentation(FilterRepresentation representation)
	{
		representation.setFilter(this);
		this.representation = representation;
	}
	
	public FilterRepresentation getRepresentation()
	{
		return representation;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * 
	 * @return
	 */
	public String getLabel()
	{
		return filter.getLabel();
	}

	/**
	 * Set the items this filter should use as input
	 * 
	 * @param input
	 */
	@SuppressWarnings("unchecked")
	public void setInput(IVirtualArray<?,?,?> input)
	{
		this.input = (IVirtualArray<?,DeltaType,?>)input;
		output = this.input.clone();
		output.setDelta(filter.getVADelta());
	}
	
	/**
	 * Get the items this filter received as input
	 * 
	 * @return
	 */
	public IVirtualArray<?,DeltaType,?> getInput()
	{
		return input;
	}
	
	/**
	 * Get the items which passed this filter
	 * 
	 * @return
	 */
	public IVirtualArray<?,DeltaType,?> getOutput()
	{
		return output;
	}
	
	public int getSizeVADelta()
	{
		return filter.getVADelta().size();
	}

	public void showDetailsDialog()
	{
		filter.getFilterRep().create();
	}
	
	public void triggerRemove()
	{
		RemoveFilterEvent<?> filterEvent = null;
		
		if( filter instanceof ContentFilter )
		{
			filterEvent = new RemoveContentFilterEvent();
			((RemoveContentFilterEvent)filterEvent).setFilter((ContentFilter)filter);
		}
		else if( filter instanceof StorageFilter )
		{
			filterEvent = new RemoveStorageFilterEvent();
			((RemoveStorageFilterEvent)filterEvent).setFilter((StorageFilter)filter);
		}
		else
		{
			System.err.println(getClass()+"::triggerRemove(): Unimplemented...");
		}
		
		if( filterEvent != null )
		{
			filterEvent.setDataDomainType(filter.getDataDomain().getDataDomainType());			
			GeneralManager.get().getEventPublisher().triggerEvent(filterEvent);
		}
	}
	
	public void triggerMove(int offset)
	{
		MoveFilterEvent<?> filterEvent = null;
		
		if( filter instanceof ContentFilter )
		{
			filterEvent = new MoveContentFilterEvent();
			((MoveContentFilterEvent)filterEvent).setFilter((ContentFilter)filter);
		}
		else if( filter instanceof StorageFilter )
		{
			filterEvent = new MoveStorageFilterEvent();
			((MoveStorageFilterEvent)filterEvent).setFilter((StorageFilter)filter);
		}
		else
		{
			System.err.println(getClass()+"::triggerMove(): Unimplemented...");
		}
		
		if( filterEvent != null )
		{
			filterEvent.setDataDomainType(filter.getDataDomain().getDataDomainType());
			((MoveFilterEvent<?>)filterEvent).setOffset(offset);
			GeneralManager.get().getEventPublisher().triggerEvent(filterEvent);
		}
	}

	public int getPickingID()
	{
		return pickingId;
	}

	@Override
	public void handleDragOver(GL2 gl, Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void handleDrop(GL2 gl, Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY, DragAndDropController dragAndDropController)
	{
		// TODO Auto-generated method stub
	}
}
