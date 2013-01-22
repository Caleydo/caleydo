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
package org.caleydo.core.data.collection.column.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorManager;

/**
 * @author ArrayList
 *
 */
public class CategoryDescriptions<CategoryType extends Comparable<CategoryType>> implements
		Iterable<CategoryProperty<CategoryType>> {

	public enum ECategoryType {
		ORDINAL, NOMINAL;
	}

	private ECategoryType categoryType = ECategoryType.NOMINAL;

	private List<CategoryProperty<CategoryType>> categoryProperties = new ArrayList<>();
	@XmlTransient
	private HashMap<CategoryType, CategoryProperty<CategoryType>> hashCategoryToProperties = new HashMap<>();

	/**
	 * @param categoryType
	 *            setter, see {@link categoryType}
	 */
	public void setCategoryType(ECategoryType categoryType) {
		this.categoryType = categoryType;
	}

	/**
	 * @return the categoryType, see {@link #categoryType}
	 */
	public ECategoryType getCategoryType() {
		return categoryType;
	}

	public void addCategoryProperties(CategoryProperty<CategoryType> categoryProperty) {
		categoryProperties.add(categoryProperty);
		hashCategoryToProperties.put(categoryProperty.getCategory(), categoryProperty);

	}

	/**
	 * @param categoryProperties
	 *            setter, see {@link categoryProperties}
	 */
	public void setCategoryProperties(List<CategoryProperty<CategoryType>> categoryProperties) {
		this.categoryProperties = categoryProperties;
		for (CategoryProperty<CategoryType> property : categoryProperties) {
			hashCategoryToProperties.put(property.getCategory(), property);
		}
	}

	/**
	 * @return the categoryProperties, see {@link #categoryProperties}
	 */
	public List<CategoryProperty<CategoryType>> getCategoryProperties() {
		return Collections.unmodifiableList(categoryProperties);
	}

	@Override
	public Iterator<CategoryProperty<CategoryType>> iterator() {
		return Collections.unmodifiableList(categoryProperties).iterator();
	}

	public int size() {
		return categoryProperties.size();
	}

	/**
	 * Initializes the Description as nominal with automatic qualitative colors. Sorts categories based on the
	 * comparable implementation of CategoryType.
	 *
	 * @param unsortedCategories
	 */
	public void autoInitialize(Collection<CategoryType> unsortedCategories) {
		ArrayList<CategoryType> categories = new ArrayList<>(unsortedCategories);
		Collections.sort(categories);

		List<Color> colors = ColorManager.get().getColorList(ColorManager.QUALITATIVE_COLORS);
		for (int i = 0; i < categories.size(); i++) {
			addCategoryProperties(new CategoryProperty<>(categories.get(i), colors.get(i)));
		}
	}

	/**
	 * Returns the properties of a category for the category
	 *
	 * @param category
	 * @return
	 */
	public CategoryProperty<CategoryType> getCategoryProperty(Object category) {
		return hashCategoryToProperties.get(category);
	}

	@Override
	public String toString() {
		String categories = "";
		for (CategoryProperty<CategoryType> categoryProperty : categoryProperties) {
			categories += categoryProperty.toString() + " ";
		}
		return categories;
	}

}
