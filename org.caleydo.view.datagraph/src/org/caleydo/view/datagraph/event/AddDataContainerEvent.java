package org.caleydo.view.datagraph.event;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.AEvent;

public class AddDataContainerEvent extends AEvent {

	private String recordPerspectiveID;
	private String dimensionPerspectiveID;
	private ATableBasedDataDomain dataDomain;
	private boolean createDimensionPerspective;
	private Group group;
	private DimensionVirtualArray dimensionVA;

	public AddDataContainerEvent(ATableBasedDataDomain dataDomain,
			String recordPerspectiveID, String dimensionPerspectiveID,
			boolean createDimensionPerspective, DimensionVirtualArray dimensionVA, Group group) {
		this.setDataDomain(dataDomain);
		this.setRecordPerspectiveID(recordPerspectiveID);
		this.setDimensionPerspectiveID(dimensionPerspectiveID);
		this.setCreateDimensionPerspective(createDimensionPerspective);
		this.setGroup(group);
		this.setDimensionVA(dimensionVA);
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

	public void setGroup(Group group) {
		this.group = group;
	}

	public Group getGroup() {
		return group;
	}

	public void setDimensionVA(DimensionVirtualArray dimensionVA) {
		this.dimensionVA = dimensionVA;
	}

	public DimensionVirtualArray getDimensionVA() {
		return dimensionVA;
	}

}
