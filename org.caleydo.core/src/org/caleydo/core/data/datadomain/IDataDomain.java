/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.datadomain;

import java.util.Set;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.util.base.ILabelHolder;
import org.caleydo.core.util.color.Color;

/**
 * Data domains are the unique points of coordinations for views and its data.
 * Genetic data is one example - another is a more generic one where Caleydo can
 * load arbitrary tabular data but without any special features of genetic
 * analysis.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public interface IDataDomain
	extends ILabelHolder {

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

	/**
	 * @return Returns if a data domain should be serialized
	 */
	public boolean isSerializeable();
}
