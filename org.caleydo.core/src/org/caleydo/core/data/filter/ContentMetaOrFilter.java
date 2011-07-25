package org.caleydo.core.data.filter;

import java.util.ArrayList;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;

/**
 * Combines multiple filters with and boolean OR
 * 
 * @author Thomas Geymayer
 *
 */
public class ContentMetaOrFilter
	extends ContentFilter implements MetaFilter<ContentFilter> {

	ArrayList<ContentFilter> filterList = new ArrayList<ContentFilter>();
	
	public ContentMetaOrFilter()
	{
		setLabel("OR Compound");
	}
	
	@Override
	public ArrayList<ContentFilter> getFilterList() {
		return filterList;
	}
	
	@Override
	public void setVADelta(ContentVADelta vaDelta) {
		throw new RuntimeException("ContentMetaOrFilter::setDelta() not allowed."); 
	}
	
	public void updateDelta()
	{
		ContentVADelta vaDeltaAll =
			new ContentVADelta(DataTable.RECORD, dataDomain.getContentIDType());
		
		for (ContentFilter filter : filterList)
			vaDeltaAll.append(filter.getVADelta());
		
		ContentVADelta vaDelta =
			new ContentVADelta(DataTable.RECORD, dataDomain.getContentIDType());

		for (VADeltaItem vaDeltaItem : vaDeltaAll.getAllItems())
		{
			boolean filteredByAll = true;
			
			for (ContentFilter filter : filterList)
			{
				if( !filter.getVADelta().getAllItems().contains(vaDeltaItem) )
				{
					filteredByAll = false;
					break;
				}
			}
			
			if( filteredByAll )
				vaDelta.add(vaDeltaItem);
		}
		
		super.setVADelta(vaDelta);
	}

}
