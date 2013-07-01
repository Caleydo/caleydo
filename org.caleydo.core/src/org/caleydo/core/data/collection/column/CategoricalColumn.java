/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.collection.column;

import java.util.Set;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoricalContainer;
import org.caleydo.core.io.DataDescription;

/**
 * @author Alexander Lex
 *
 */
public class CategoricalColumn<CATEGORY_TYPE extends Comparable<CATEGORY_TYPE>> extends
		AColumn<CategoricalContainer<CATEGORY_TYPE>, CATEGORY_TYPE> {

	public CategoricalColumn(DataDescription dataDescription) {
		super(dataDescription);
	}

	public void setCategoryDescriptions(CategoricalClassDescription<CATEGORY_TYPE> categoryDescriptions) {
		rawContainer.setCategoryDescritions(categoryDescriptions);
	}


	@Override
	public CategoricalClassDescription<CATEGORY_TYPE> getDataClassSpecificDescription() {
		return rawContainer.getCategoryDescriptions();
	}

	/** Returns a set of all registered categories */
	public Set<CATEGORY_TYPE> getCategories() {
		return rawContainer.getCategories();
	}

	/**
	 * returns the number of matches
	 * 
	 * @param category
	 * @return
	 */
	public int getNumberOfMatches(Object category) {
		return rawContainer.getNumberOfMatches(category);
	}
}
