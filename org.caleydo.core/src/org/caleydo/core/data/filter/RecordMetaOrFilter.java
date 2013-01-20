/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
