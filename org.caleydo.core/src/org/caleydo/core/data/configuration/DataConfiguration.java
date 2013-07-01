/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.core.data.configuration;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;

/**
 * Bean holding a set of configuration for data properties, thereby specifying
 * exactly which data to use.
 *
 * @author Alexander Lex
 */
public class DataConfiguration {

	private ATableBasedDataDomain dataDomain;
	private Perspective recordPerspective;
	private Perspective dimensionPerspective;

	/**
	 *
	 */
	public DataConfiguration() {
	}

	/**
	 * @return the dataDomain, see {@link #dataDomain}
	 */
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	/**
	 * @param dataDomain
	 *            setter, see {@link #dataDomain}
	 */
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	/**
	 * @return the recordPerspective, see {@link #recordPerspective}
	 */
	public Perspective getRecordPerspective() {
		return recordPerspective;
	}

	/**
	 * @param recordPerspective
	 *            setter, see {@link #recordPerspective}
	 */
	public void setRecordPerspective(Perspective recordPerspective) {
		this.recordPerspective = recordPerspective;
	}

	/**
	 * @return the dimensionPerspective, see {@link #dimensionPerspective}
	 */
	public Perspective getDimensionPerspective() {
		return dimensionPerspective;
	}

	/**
	 * @param dimensionPerspective
	 *            setter, see {@link #dimensionPerspective}
	 */
	public void setDimensionPerspective(Perspective dimensionPerspective) {
		this.dimensionPerspective = dimensionPerspective;
	}

}
