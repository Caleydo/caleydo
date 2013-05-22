/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
