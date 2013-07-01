/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer.tcga.model;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.data.importer.tcga.EDataSetType;

/**
 * helper class to keep tracking of data set type for a {@link DataSetDescription}
 * 
 * @author Samuel Gratzl
 * 
 */
public class TCGADataSet {
	private final DataSetDescription description;
	private final EDataSetType type;
	private ATableBasedDataDomain dataDomain;

	/**
	 * @param description
	 * @param type
	 */
	public TCGADataSet(DataSetDescription description, EDataSetType type) {
		super();
		this.description = description;
		this.type = type;
	}

	/**
	 * @return the description, see {@link #description}
	 */
	public DataSetDescription getDescription() {
		return description;
	}

	/**
	 * @return the type, see {@link #type}
	 */
	public EDataSetType getType() {
		return type;
	}

	/**
	 * @param dataDomain
	 *            setter, see {@link dataDomain}
	 */
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	/**
	 * @return the dataDomain, see {@link #dataDomain}
	 */
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

}
