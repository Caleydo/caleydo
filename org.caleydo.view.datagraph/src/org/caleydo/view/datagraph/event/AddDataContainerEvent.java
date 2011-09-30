package org.caleydo.view.datagraph.event;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.event.AEvent;

public class AddDataContainerEvent extends AEvent {

	private String recordPerspectiveID;
	private String dimensionPerspectiveID;
	private ATableBasedDataDomain dataDomain;

	public AddDataContainerEvent(ATableBasedDataDomain dataDomain,
			String recordPerspectiveID, String dimensionPerspectiveID) {
		this.setDataDomain(dataDomain);
		this.setRecordPerspectiveID(recordPerspectiveID);
		this.setDimensionPerspectiveID(dimensionPerspectiveID);
	}

	@Override
	public boolean checkIntegrity() {
		return (recordPerspectiveID != null)
				&& (dimensionPerspectiveID != null) && (dataDomain != null);
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

}
