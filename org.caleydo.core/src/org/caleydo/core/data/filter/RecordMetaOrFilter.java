/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.filter;

import java.util.ArrayList;

import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;

/**
 * Combines multiple filters with and boolean OR
 *
 * @author Thomas Geymayer
 */
public class RecordMetaOrFilter
 extends Filter implements MetaFilter {

	ArrayList<Filter> filterList = new ArrayList<Filter>();

	/**
	 * Should only be used for de-serzialization
	 */
	// public RecordMetaOrFilter()
	// {
	// setLabel("OR Compound");
	// }

	public RecordMetaOrFilter(String perspectiveID) {
		super(perspectiveID);
		setLabel("OR Compound");
	}

	@Override
	public ArrayList<Filter> getFilterList() {
		return filterList;
	}

	@Override
	public void setVADelta(VirtualArrayDelta vaDelta) {
		throw new RuntimeException("ContentMetaOrFilter::setDelta() not allowed.");
	}

	public void updateDelta(String perspectiveID) {
		VirtualArrayDelta vaDeltaAll = new VirtualArrayDelta(perspectiveID, dataDomain.getRecordIDType());

		for (Filter filter : filterList)
			vaDeltaAll.append(filter.getVADelta());

		VirtualArrayDelta vaDelta = new VirtualArrayDelta(perspectiveID, dataDomain.getRecordIDType());

		for (VADeltaItem vaDeltaItem : vaDeltaAll.getAllItems()) {
			boolean filteredByAll = true;

			for (Filter filter : filterList) {
				if (!filter.getVADelta().getAllItems().contains(vaDeltaItem)) {
					filteredByAll = false;
					break;
				}
			}

			if (filteredByAll)
				vaDelta.add(vaDeltaItem);
		}

		super.setVADelta(vaDelta);
	}

}
