/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
