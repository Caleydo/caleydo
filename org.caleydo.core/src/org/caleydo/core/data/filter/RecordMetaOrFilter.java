package org.caleydo.core.data.filter;

import java.util.ArrayList;

import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;

/**
 * Combines multiple filters with and boolean OR
 * 
 * @author Thomas Geymayer
 *
 */
public class RecordMetaOrFilter
	extends RecordFilter implements MetaFilter<RecordFilter> {

	ArrayList<RecordFilter> filterList = new ArrayList<RecordFilter>();
	
	public RecordMetaOrFilter()
	{
		setLabel("OR Compound");
	}
	
	@Override
	public ArrayList<RecordFilter> getFilterList() {
		return filterList;
	}
	
	@Override
	public void setVADelta(RecordVADelta vaDelta) {
		throw new RuntimeException("ContentMetaOrFilter::setDelta() not allowed."); 
	}
	
	public void updateDelta(String perspectiveID)
	{
		RecordVADelta vaDeltaAll =
			new RecordVADelta(perspectiveID, dataDomain.getRecordIDType());
		
		for (RecordFilter filter : filterList)
			vaDeltaAll.append(filter.getVADelta());
		
		RecordVADelta vaDelta =
			new RecordVADelta(perspectiveID, dataDomain.getRecordIDType());

		for (VADeltaItem vaDeltaItem : vaDeltaAll.getAllItems())
		{
			boolean filteredByAll = true;
			
			for (RecordFilter filter : filterList)
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
