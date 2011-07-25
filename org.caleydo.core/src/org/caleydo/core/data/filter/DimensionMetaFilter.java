package org.caleydo.core.data.filter;

import java.util.ArrayList;

public class DimensionMetaFilter
	extends DimensionFilter implements MetaFilter<DimensionFilter> {

	ArrayList<DimensionFilter> filterList = new ArrayList<DimensionFilter>();
	
	@Override
	public ArrayList<DimensionFilter> getFilterList() {
		return filterList;
	}

}
