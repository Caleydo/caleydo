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
package org.caleydo.core.data.collection.table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.data.collection.column.AColumn;
import org.caleydo.core.data.collection.column.CategoricalColumn;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.util.color.mapping.ColorMapper;
import org.caleydo.core.util.color.mapping.ColorMarkerPoint;

/**
 * Extension of {@link Table} to add functionality specific to homogeneous categorical tables, such as a joint set of
 * categories or a joint order of categories.
 *
 * @author Alexander Lex
 */
public class CategoricalTable<CategoryType extends Comparable<CategoryType>> extends Table {
	private CategoricalClassDescription<CategoryType> categoricalClassDescription;

	/**
	 * @param dataDomain
	 */
	public CategoricalTable(ATableBasedDataDomain dataDomain) {
		super(dataDomain);
	}

	@Override
	public boolean isDataHomogeneous() {
		return true;
	}

	@Override
	protected void normalize() {
		if (categoricalClassDescription == null) {
			Set<CategoryType> uniqueCategories = new HashSet<>();
			for (AColumn<?, ?> column : columns) {
				@SuppressWarnings("unchecked")
				CategoricalColumn<CategoryType> catCol = (CategoricalColumn<CategoryType>) column;
				uniqueCategories.addAll(catCol.getCategories());
			}
			categoricalClassDescription = new CategoricalClassDescription<>();
			categoricalClassDescription.autoInitialize(uniqueCategories);
			for (AColumn<?, ?> column : columns) {
				@SuppressWarnings("unchecked")
				CategoricalColumn<CategoryType> catCol = (CategoricalColumn<CategoryType>) column;
				catCol.setCategoryDescriptions(categoricalClassDescription);
			}

		}
		super.normalize();
	}

	public void setCategoryDescritions(CategoricalClassDescription<CategoryType> categoryDescriptions) {
		this.categoricalClassDescription = categoryDescriptions;
		// for (AColumn<?, ?> column : columns) {
		// @SuppressWarnings("unchecked")
		// CategoricalColumn<CategoryType> categoricalColum = (CategoricalColumn<CategoryType>) column;
		// categoricalColum.setCategoryDescritions(categoricalClassDescription);
		// }
	}

	/**
	 * @return the categoricalClassDescription, see {@link #categoricalClassDescription}
	 */
	public CategoricalClassDescription<CategoryType> getCategoryDescriptions() {
		return categoricalClassDescription;
	}

	/**
	 * Creates a new color map based on the colors and the order of {@link #categoricalClassDescription}.
	 *
	 * @return
	 */
	public ColorMapper createColorMapper() {
		ColorMapper mapper = new ColorMapper();
		float normalizedDistance = 0;
		if (categoricalClassDescription.size() > 1) {
			normalizedDistance = 1f / (categoricalClassDescription.size() - 1);
		}
		float currentDistance = 0;
		ArrayList<ColorMarkerPoint> markerPoints = new ArrayList<>(categoricalClassDescription.size());
		for (CategoryProperty<?> property : categoricalClassDescription) {
			markerPoints.add(new ColorMarkerPoint(currentDistance, property.getColor()));
			currentDistance += normalizedDistance;
		}

		mapper.setMarkerPoints(markerPoints);
		return mapper;

	}
}
