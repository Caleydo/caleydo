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
package org.caleydo.view.dvi.event;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.AEvent;

public class CreateTablePerspectiveEvent extends AEvent {

	private String recordPerspectiveID;
	private String dimensionPerspectiveID;
	private ATableBasedDataDomain dataDomain;
	private boolean createDimensionPerspective;
	private boolean createRecordPerspective;
	private Group recordGroup;
	private RecordVirtualArray recordVA;
	private Group dimensionGroup;
	private DimensionVirtualArray dimensionVA;

	public CreateTablePerspectiveEvent(ATableBasedDataDomain dataDomain,
			String recordPerspectiveID, boolean createRecordPerspective,
			RecordVirtualArray recordVA, Group recordGroup,
			String dimensionPerspectiveID, boolean createDimensionPerspective,
			DimensionVirtualArray dimensionVA, Group dimensionGroup) {
		this.setDataDomain(dataDomain);
		this.setRecordPerspectiveID(recordPerspectiveID);
		this.setDimensionPerspectiveID(dimensionPerspectiveID);
		this.setCreateDimensionPerspective(createDimensionPerspective);
		this.setDimensionGroup(dimensionGroup);
		this.setDimensionVA(dimensionVA);
		this.setCreateRecordPerspective(createRecordPerspective);
		this.setRecordGroup(recordGroup);
		this.setRecordVA(recordVA);
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	public void setRecordPerspectiveID(String recordPerspectiveID) {
		this.recordPerspectiveID = recordPerspectiveID;
	}

	public String getRecordPerspectiveID() {
		return recordPerspectiveID;
	}

	public void setDimensionPerspectiveID(String dimensionPerspectiveID) {
		this.dimensionPerspectiveID = dimensionPerspectiveID;
	}

	public String getDimensionPerspectiveID() {
		return dimensionPerspectiveID;
	}

	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	public void setCreateDimensionPerspective(boolean createDimensionPerspective) {
		this.createDimensionPerspective = createDimensionPerspective;
	}

	public boolean isCreateDimensionPerspective() {
		return createDimensionPerspective;
	}

	public void setDimensionGroup(Group group) {
		this.dimensionGroup = group;
	}

	public Group getDimensionGroup() {
		return dimensionGroup;
	}

	public void setDimensionVA(DimensionVirtualArray dimensionVA) {
		this.dimensionVA = dimensionVA;
	}

	public DimensionVirtualArray getDimensionVA() {
		return dimensionVA;
	}

	public boolean isCreateRecordPerspective() {
		return createRecordPerspective;
	}

	public void setCreateRecordPerspective(boolean createRecordPerspective) {
		this.createRecordPerspective = createRecordPerspective;
	}

	public Group getRecordGroup() {
		return recordGroup;
	}

	public void setRecordGroup(Group recordGroup) {
		this.recordGroup = recordGroup;
	}

	public RecordVirtualArray getRecordVA() {
		return recordVA;
	}

	public void setRecordVA(RecordVirtualArray recordVA) {
		this.recordVA = recordVA;
	}

}
