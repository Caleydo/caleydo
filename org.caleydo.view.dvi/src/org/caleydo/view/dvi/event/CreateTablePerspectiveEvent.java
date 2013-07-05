/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.event;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.AEvent;

public class CreateTablePerspectiveEvent extends AEvent {

	private String recordPerspectiveID;
	private String dimensionPerspectiveID;
	private ATableBasedDataDomain dataDomain;
	private boolean createDimensionPerspective;
	private boolean createRecordPerspective;
	private Group recordGroup;
	private VirtualArray recordVA;
	private Group dimensionGroup;
	private VirtualArray dimensionVA;

	public CreateTablePerspectiveEvent(ATableBasedDataDomain dataDomain,
			String recordPerspectiveID, boolean createRecordPerspective,
			VirtualArray recordVA, Group recordGroup,
			String dimensionPerspectiveID, boolean createDimensionPerspective,
			VirtualArray dimensionVA, Group dimensionGroup) {
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

	public void setDimensionVA(VirtualArray dimensionVA) {
		this.dimensionVA = dimensionVA;
	}

	public VirtualArray getDimensionVA() {
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

	public VirtualArray getRecordVA() {
		return recordVA;
	}

	public void setRecordVA(VirtualArray recordVA) {
		this.recordVA = recordVA;
	}

}
