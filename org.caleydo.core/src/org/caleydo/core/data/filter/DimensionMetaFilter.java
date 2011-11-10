package org.caleydo.core.data.filter;

import java.util.ArrayList;

public class DimensionMetaFilter
	extends DimensionFilter
	implements MetaFilter<DimensionFilter> {

	ArrayList<DimensionFilter> filterList = new ArrayList<DimensionFilter>();

	/**
	 * Should only be used for de-serialization
	 */
	// public DimensionMetaFilter() {
	//
	// }

	public DimensionMetaFilter(String perspectiveID) {
		super(perspectiveID);
	}

	@Override
	public ArrayList<DimensionFilter> getFilterList() {
		return filterList;
	}

}
