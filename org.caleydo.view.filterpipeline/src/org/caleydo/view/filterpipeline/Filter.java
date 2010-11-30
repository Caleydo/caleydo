package org.caleydo.view.filterpipeline;

import org.caleydo.core.data.filter.representation.AFilterRepresentation;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;

/**
 * Represents a filter
 * 
 * @author Thomas Geymayer
 *
 */
public class Filter
{
	private int id;
	private String label;
	private int countFilteredItems;
	private AFilterRepresentation<?, ?> filterRepresentation;	

	/**
	 * 
	 * @param id
	 * @param label
	 * @param countFilteredItems
	 * @param aFilterRepresentation 
	 */
	public Filter(int id, String label, int countFilteredItems, AFilterRepresentation<?, ?> filterRepresentation)
	{
		this.id = id;
		this.label = label;
		this.countFilteredItems = countFilteredItems;
		this.filterRepresentation = filterRepresentation;
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
		return label;
	}

	/**
	 * 
	 * @return
	 */
	public int getCountFilteredItems()
	{
		return countFilteredItems;
	}
	
	public void showDetailsDialog()
	{
		filterRepresentation.create();
	}
}
