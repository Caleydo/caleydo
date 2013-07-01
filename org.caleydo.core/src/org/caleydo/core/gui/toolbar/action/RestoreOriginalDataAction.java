/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui.toolbar.action;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.gui.SimpleAction;

public class RestoreOriginalDataAction extends SimpleAction {

	public static final String LABEL = "Restore original data";
	public static final String ICON = "resources/icons/general/restore.png";

	private final String recordPerspectiveID;

	public RestoreOriginalDataAction(String recordPerspectiveID) {
		super(LABEL, ICON);
		this.recordPerspectiveID = recordPerspectiveID;
	}

	@Override
	public void run() {
		super.run();

		for (IDataDomain dataDomain : DataDomainManager.get().getDataDomains()) {
			if (dataDomain instanceof ATableBasedDataDomain)
				((ATableBasedDataDomain) dataDomain).getTable().getDefaultRecordPerspective();
			System.out.println("Reset not implemented" + recordPerspectiveID);
		}
	}
}
