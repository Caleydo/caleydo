package org.caleydo.core.data.filter;

import java.util.ArrayList;

public class RecordMetaFilter
	extends RecordFilter
	implements MetaFilter<RecordFilter> {

	ArrayList<RecordFilter> filterList = new ArrayList<RecordFilter>();

	/**
	 * Should only be used for de-serialization
	 */
	// public RecordMetaFilter() {
	// }

	public RecordMetaFilter(String perspectiveID) {
		super(perspectiveID);
	}

	@Override
	public ArrayList<RecordFilter> getFilterList() {
		return filterList;
	}

}
