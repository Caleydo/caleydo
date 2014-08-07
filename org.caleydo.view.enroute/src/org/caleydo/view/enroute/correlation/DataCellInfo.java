/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;

/**
 * @author Christian
 *
 */
public class DataCellInfo {

	public int cellID;
	public ATableBasedDataDomain dataDomain;
	public Perspective columnPerspective;
	public IDType rowIDType;
	public int rowID;

	/**
	 * @param dataDomain
	 * @param columnPerspective
	 * @param rowIDType
	 * @param rowID
	 */
	public DataCellInfo(int cellID, ATableBasedDataDomain dataDomain, Perspective columnPerspective, IDType rowIDType,
			int rowID) {
		super();
		this.cellID = cellID;
		this.dataDomain = dataDomain;
		this.columnPerspective = columnPerspective;
		this.rowIDType = rowIDType;
		this.rowID = rowID;
	}

	public String getDataDomainLabel() {
		return dataDomain.getLabel();
	}

	public String getGroupLabel() {
		return columnPerspective.getLabel();
	}

	public String getRowLabel() {
		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(rowIDType);
		Set<String> humanReadableIDs = mappingManager.getIDAsSet(rowIDType, rowIDType.getIDCategory()
				.getHumanReadableIDType(), rowID);
		return humanReadableIDs.iterator().next();
	}

}
