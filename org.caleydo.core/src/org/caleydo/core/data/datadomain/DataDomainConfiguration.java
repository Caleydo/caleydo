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
package org.caleydo.core.data.datadomain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;

/**
 * Configuration for {@link ATableBasedDataDomain}. This initializes the parts
 * of the configuration of the data domain which are specific to the type of the
 * data set loaded. Examples are {@link IDType}s, and {@link IDCategory}s.
 * <p>
 * TODO: check whether a valid configuration has been set
 * </p>
 * 
 * @author Alexander Lex
 */
public class DataDomainConfiguration {

	/**
	 * Flag determining whether this configuration was created as a default
	 * configuration by a data domain (true) or was manually specified (false,
	 * the default). We may throw away defaults and rebuild them (e.g. when
	 * column - dimension association is changed).
	 */
	@XmlTransient
	boolean isDefaultConfiguration = false;

	@XmlElement
	String recordIDCategory;
	@XmlElement
	String dimensionIDCategory;

	/**
	 * @param isDefaultConfiguration
	 *            setter, see {@link #isDefaultConfiguration}.
	 */
	public void setDefaultConfiguration(boolean isDefaultConfiguration) {
		this.isDefaultConfiguration = isDefaultConfiguration;
	}

	/**
	 * @return the isDefaultConfiguration, see {@link #isDefaultConfiguration}
	 */
	public boolean isDefaultConfiguration() {
		return isDefaultConfiguration;
	}

	/**
	 * @param recordIDCategory
	 *            setter, see {@link #recordIDCategory}
	 */
	public void setRecordIDCategory(String recordIDCategory) {
		this.recordIDCategory = recordIDCategory;
	}

	/**
	 * @param dimensionIDCategory
	 *            setter, see {@link #dimensionIDCategory}
	 */
	public void setDimensionIDCategory(String dimensionIDCategory) {
		this.dimensionIDCategory = dimensionIDCategory;
	}

}
