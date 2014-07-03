/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view;

import org.caleydo.core.data.perspective.table.TablePerspective;

/**
 * Bean class that holds data that needs to be passed to a view when it is opened via the RCP open mechanism.
 * 
 * @author Marc Streit
 */
public class RCPViewInitializationData {

	/** The ID of the data domain that will be initially shown in the view */
	private String dataDomainID;

	private TablePerspective tablePerspective;

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

	public TablePerspective getTablePerspective() {
		return tablePerspective;
	}

	public void setTablePerspective(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
	}
}
