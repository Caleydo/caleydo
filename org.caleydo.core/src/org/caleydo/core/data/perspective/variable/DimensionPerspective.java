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
import org.caleydo.core.data.filter.DimensionFilterManager;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.data.virtualarray.group.DimensionGroupList;

/**
 * Implementation of {@link AVariablePerspective} for dimensions.
 * 
 * @author Alexander Lex
 */
@XmlRootElement
public class DimensionPerspective
	extends
	AVariablePerspective<DimensionVirtualArray, DimensionGroupList, DimensionVADelta, DimensionFilterManager> {

	public DimensionPerspective() {
	}

	public DimensionPerspective(ATableBasedDataDomain dataDomain) {
		super(dataDomain);
	}

	@Override
	protected void init() {
		// if this perspective is de-serialized the perspectiveID is already set.
		if (perspectiveID == null)
			perspectiveID = "DimensionPerspective_" + UUID.randomUUID();
		filterManager = new DimensionFilterManager(dataDomain, this);
		idType = dataDomain.getDimensionIDType();
	}

	@Override
	protected DimensionGroupList createGroupList() {
		return new DimensionGroupList();
	}

	@Override
	protected void createFilterManager() {
		filterManager = new DimensionFilterManager(dataDomain, this);
	}

	@Override
	protected DimensionVirtualArray newConcreteVirtualArray(List<Integer> indexList) {
		return new DimensionVirtualArray(idType, indexList);
	}

	@Override
	protected String getElementLabel(Integer id) {
		return dataDomain.getDimensionLabel(id);
	}

	@Override
	protected List<Integer> getIDList() {
		return dataDomain.getTable().getColumnIDList();
	}
}
