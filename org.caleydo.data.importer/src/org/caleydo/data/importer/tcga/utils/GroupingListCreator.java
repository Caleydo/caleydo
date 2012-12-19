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
package org.caleydo.data.importer.tcga.utils;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.perspective.variable.RecordPerspective;

public class GroupingListCreator {

	public static String getRecordGroupingList(ATableBasedDataDomain dataDomain) {

		String recordGroupings = "";

		for (String recordPerspectiveID : dataDomain.getRecordPerspectiveIDs()) {
			RecordPerspective recordPerspective = dataDomain.getTable().getRecordPerspective(
					recordPerspectiveID);

			if (recordPerspective.isPrivate())
				continue;
			if (recordPerspective.getLabel().equals("Default"))
				continue;

			recordGroupings += "\"" + recordPerspective.getLabel() + "\",";
		}

		// remove last comma
		if (recordGroupings.length() > 1)
			recordGroupings = recordGroupings.substring(0, recordGroupings.length() - 1);

		return recordGroupings;
	}

	public static String getDimensionGroupingList(ATableBasedDataDomain dataDomain) {
		String dimensionGroupings = "";
		for (String dimensionPerspectiveID : dataDomain.getDimensionPerspectiveIDs()) {
			DimensionPerspective dimensionPerspective = dataDomain.getTable()
					.getDimensionPerspective(dimensionPerspectiveID);
			if (dimensionPerspective.isPrivate()) {
				continue;
			}
			if (dimensionPerspective.getLabel().equals("Default"))
				continue;

			dimensionGroupings += "\"" + dimensionPerspective.getLabel() + "\",";
		}

		// remove last comma
		if (dimensionGroupings.length() > 1)
			dimensionGroupings = dimensionGroupings.substring(0,
					dimensionGroupings.length() - 1);

		return dimensionGroupings;
	}
}
