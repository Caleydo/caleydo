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
