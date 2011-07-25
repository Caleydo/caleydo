package org.caleydo.core.data.filter;

import java.util.ArrayList;

public class RecordMetaFilter
	extends RecordFilter implements MetaFilter<RecordFilter> {

	ArrayList<RecordFilter> filterList = new ArrayList<RecordFilter>();
	
	@Override
	public ArrayList<RecordFilter> getFilterList() {
		return filterList;
	}

}
