package org.caleydo.core.data.filter;

import java.util.ArrayList;

public class StorageMetaFilter
	extends StorageFilter implements MetaFilter<StorageFilter> {

	ArrayList<StorageFilter> filterList = new ArrayList<StorageFilter>();
	
	@Override
	public ArrayList<StorageFilter> getFilterList() {
		return filterList;
	}

}
