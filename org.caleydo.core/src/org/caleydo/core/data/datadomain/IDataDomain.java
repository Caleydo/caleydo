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
package org.caleydo.core.data.datadomain;

import java.util.Set;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * Use cases are the unique points of coordinations for views and its data.
 * Genetic data is one example - another is a more generic one where Caleydo can
 * load arbitrary tabular data but without any special features of genetic
 * analysis.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public interface IDataDomain {

	/**
	 * Returns the qualified name of the concrete data domain
	 */
	public String getDataDomainID();

	/**
	 * Set the dataDomain Type.
	 * 
	 * @param dataDomainType
	 */
	public void setDataDomainID(String dataDomainType);

	/**
	 * Returns the icon representing the data contained in this domain
	 */
	public EIconTextures getIcon();

	/**
	 * Gets the parameters for loading the data-{@link Set} contained in this
	 * use case
	 * 
	 * @return parameters for loading the data-{@link Set} of this use case
	 */
	public DataSetDescription getDataSetDescription();

	/**
	 * Sets the parameters for loading the data-{@link Set} contained in this
	 * use case
	 * 
	 * @param dataSetDescription parameters for loading the data-{@link Set} of
	 *            this use case
	 */
	public void setDataSetDescription(DataSetDescription dataSetDescription);

	// /**
	// * @return The dimension groups that have been created for this
	// IDataDomain object (data set).
	// */
	// public List<TablePerspective> getDimensionGroups();
	//
	// /**
	// * Sets the dimension groups for this IDataDomain object (data set).
	// *
	// * @param dimensionGroups
	// */
	// public void setDimensionGroups(List<TablePerspective> dimensionGroups);
	//
	// /**
	// * Adds a dimension group to this IDataDomain object (data set).
	// *
	// * @param dimensionGroup
	// */
	// public void addDimensionGroup(TablePerspective dimensionGroup);

	public String getDataDomainType();

	public void setDataDomainType(String dataDomainType);

	public Color getColor();

	/**
	 * @return The amount of data, i.e. the number of data items in the data
	 *         set.
	 */
	public int getDataAmount();

	/**
	 * @return The ID categories that are defined for this datadomain.
	 */
	public Set<IDCategory> getIDCategories();

	/**
	 * Adds an ID category to this datadomain.
	 * 
	 * @param category
	 */
	public void addIDCategory(IDCategory category);

	public String getLabel();

	/**
	 * @return Returns if a data domain should be serialized
	 */
	public boolean isSerializeable();
}
