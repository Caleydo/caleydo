package org.caleydo.core.data.filter;

import java.util.ArrayList;

public class ContentMetaFilter
	extends ContentFilter implements MetaFilter<ContentFilter> {

	ArrayList<ContentFilter> filterList = new ArrayList<ContentFilter>();
	
	@Override
	public ArrayList<ContentFilter> getFilterList() {
		return filterList;
	}

}
