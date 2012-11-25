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

import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;

/**
 * Combines multiple filters with and boolean OR
 * 
 * @author Thomas Geymayer
 */
public class RecordMetaOrFilter
	extends RecordFilter
	implements MetaFilter<RecordFilter> {

	ArrayList<RecordFilter> filterList = new ArrayList<RecordFilter>();

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
	public ArrayList<RecordFilter> getFilterList() {
		return filterList;
	}

	@Override
	public void setVADelta(RecordVADelta vaDelta) {
		throw new RuntimeException("ContentMetaOrFilter::setDelta() not allowed.");
	}

	public void updateDelta(String perspectiveID) {
		RecordVADelta vaDeltaAll = new RecordVADelta(perspectiveID, dataDomain.getRecordIDType());

		for (RecordFilter filter : filterList)
			vaDeltaAll.append(filter.getVADelta());

		RecordVADelta vaDelta = new RecordVADelta(perspectiveID, dataDomain.getRecordIDType());

		for (VADeltaItem vaDeltaItem : vaDeltaAll.getAllItems()) {
			boolean filteredByAll = true;

			for (RecordFilter filter : filterList) {
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
