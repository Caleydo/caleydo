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
package org.caleydo.core.data.perspective.variable;

import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.filter.RecordFilterManager;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;

/**
 * Implementation of {@link AVariablePerspective} for records.
 * 
 * @author Alexander Lex
 */
@XmlRootElement
public class RecordPerspective
	extends AVariablePerspective<RecordVirtualArray, RecordGroupList, RecordVADelta, RecordFilterManager> {

	public RecordPerspective() {
	}

	public RecordPerspective(ATableBasedDataDomain dataDomain) {
		super(dataDomain);
	}

	@Override
	protected void init() {
		// if this perspective is de-serialized the perspectiveID is already set.
		if (perspectiveID == null)
			perspectiveID = "RecordPerspective_" + UUID.randomUUID();
		filterManager = new RecordFilterManager(dataDomain, this);
		idType = dataDomain.getRecordIDType();
	}

	@Override
	protected RecordGroupList createGroupList() {
		return new RecordGroupList();
	}

	@Override
	protected void createFilterManager() {
		filterManager = new RecordFilterManager(dataDomain, this);
	}

	@Override
	protected RecordVirtualArray newConcreteVirtualArray(List<Integer> indexList) {
		return new RecordVirtualArray(idType, indexList);
	}

	@Override
	protected String getElementLabel(Integer id) {
		return dataDomain.getRecordLabel(id);
	}

	@Override
	protected List<Integer> getIDList() {
		return dataDomain.getTable().getRowIDList();
	}

}
