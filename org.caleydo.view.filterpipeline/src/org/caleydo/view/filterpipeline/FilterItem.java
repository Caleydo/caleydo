package org.caleydo.view.filterpipeline;

import java.util.Vector;
import org.caleydo.core.data.filter.ContentFilter;
import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.data.filter.event.RemoveContentFilterEvent;
import org.caleydo.core.data.filter.event.RemoveFilterEvent;
import org.caleydo.core.manager.GeneralManager;

/**
 * Represents a filter
 * 
 * @author Thomas Geymayer
 *
 */
public class FilterItem
{
	private int id;
	private Filter<?> filter;
	private Vector<Integer> filteredItems;

	/**
	 * 
	 * @param id
	 * @param filteredItems
	 * @param aFilterRepresentation 
	 */
	public FilterItem( int id,
			           Vector<Integer> filteredItems,
			           Filter<?> filter )
	{
		this.id = id;
		this.filteredItems = filteredItems;
		this.filter = filter;
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
	 * @return the filteredItems
	 */
	public Vector<Integer> getFilteredItems()
	{
		return filteredItems;
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
}
