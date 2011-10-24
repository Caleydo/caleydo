package org.caleydo.core.serialize;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;

/**
 * Abstract class for all serialized view representations that handle a single {@link ATableBasedDataDomain}
 * (NOT embedded views with data domains).
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public abstract class ASerializedTopLevelDataView
	extends ASerializedView {

	/**
	 * DO NOT CALL THIS CONSTRUCTOR! ONLY USED FOR DESERIALIZATION.
	 */
	public ASerializedTopLevelDataView() {
	}

	public ASerializedTopLevelDataView(String dataDomainID) {
		this.dataDomainID = dataDomainID;
	}

	protected String dataDomainID;

	/**
	 * Specifies which {@link DimensionPerspective} is used to view the data in the {@link DataTable}
	 */
	protected String dimensionPerspectiveID;

	/**
	 * Specifies which {@link recordData} is used to view the data in the {@link DataTable}
	 */
	protected String recordPerspectiveID;

	/**
	 * Sets the data domain associated with a view
	 * 
	 * @param dataDomain
	 */
	public void setDataDomainID(String dataDomainID) {
		this.dataDomainID = dataDomainID;
	}

	/**
	 * Returns the data domain a view is associated with
	 * 
	 * @return
	 */
	public String getDataDomainID() {
		return dataDomainID;
	}

	/** Set the {@link #recordPerspectiveID} */
	public void setRecordPerspectiveID(String recordPerspectiveID) {
		this.recordPerspectiveID = recordPerspectiveID;
	}

	/** Get the {@link #recordPerspectiveID} */
	public String getRecordPerspectiveID() {
		return recordPerspectiveID;
	}

	/** Set the {@link #dimensionPerspectiveID} */
	public void setDimensionPerspectiveID(String dimensionPerspectiveID) {
		this.dimensionPerspectiveID = dimensionPerspectiveID;
	}

	/** Set the {@link #dimensionPerspectiveID} */
	public String getDimensionPerspectiveID() {
		return dimensionPerspectiveID;
	}
}
