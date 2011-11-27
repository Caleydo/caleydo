package org.caleydo.core.view;

import org.caleydo.core.data.container.DataContainer;

/**
 * Bean class that holds data that needs to be passed to a view when it is opened via the RCP open mechanism.
 * 
 * @author Marc Streit
 */
public class RCPViewInitializationData {

	/** The ID of the data domain that will be initially shown in the view */
	private String dataDomainID;

	private DataContainer dataContainer;

	/**
	 * @return the dataDomainID, see {@link #dataDomainID}
	 */
	public String getDataDomainID() {
		return dataDomainID;
	}

	/**
	 * @param dataDomainID
	 *            setter, see {@link #dataDomainID}
	 */
	public void setDataDomainID(String dataDomainID) {
		this.dataDomainID = dataDomainID;
	}

	public DataContainer getDataContainer() {
		return dataContainer;
	}

	public void setDataContainer(DataContainer dataContainer) {
		this.dataContainer = dataContainer;
	}
}
