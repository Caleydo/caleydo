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
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.CategoricalColumn;
import org.caleydo.core.data.collection.table.CategoricalTable;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorManager;

/**
 * <p>
 * Description/specification of a "class" of categories (e.g., gender as class, male, female as categories).
 * </p>
 * <p>
 * This class contains an (ordered) list of {@link CategoryProperty}s which in turn contain information on the
 * individual categories and the type of category (ordinal or nominal).
 * </p>
 * <p>
 * CategoricalClassDescription can be defined either for a whole {@link CategoricalTable} when the data is homogeneous,
 * or for individual {@link CategoricalColumn}s in case a dataset is inhomogeneous.
 * </p>
 * <p>
 * This class is used at runtime for storing and providing the properties, but is also used for serialization as part of
 * the {@link DataSetDescription}.
 * </p>
 * <p>
 * See also {@link NumericalProperties}, this classes pendant for numerical data.
 * </p>
 *
 * @author Alexander Lex
 *
 */
@XmlType
public class CategoricalClassDescription<CategoryType extends Comparable<CategoryType>> implements
		Iterable<CategoryProperty<CategoryType>> {

	/** The type of the category */
	public enum ECategoryType {
		ORDINAL, NOMINAL;
	}

	private EDataType rawDataType = EDataType.INTEGER;

	private ECategoryType categoryType = ECategoryType.NOMINAL;

	/** The properties of the categories that are part of this class */
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

	/**
	 * @param rawDataType
	 *            setter, see {@link rawDataType}
	 */
	public void setRawDataType(EDataType rawDataType) {
		this.rawDataType = rawDataType;
	}

	/**
	 * @return the rawDataType, see {@link #rawDataType}
	 */
	public EDataType getRawDataType() {
		return rawDataType;
	}

	/**
	 * Add a category to this class
	 *
	 * @param categoryProperty
	 */
	public void addCategoryProperty(CategoryProperty<CategoryType> categoryProperty) {
		categoryProperties.add(categoryProperty);
		hashCategoryToProperties.put(categoryProperty.getCategory(), categoryProperty);

	}

	/** Shorthand for {@link #addCategoryProperty(CategoryProperty)} */
	public void addCategoryProperty(CategoryType category, String categoryName, Color color) {
		CategoryProperty<CategoryType> property = new CategoryProperty<>(category, categoryName, color);
		addCategoryProperty(property);
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
		return categoryProperties;
	}

	@Override
	public Iterator<CategoryProperty<CategoryType>> iterator() {
		return categoryProperties.iterator();
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
			addCategoryProperty(new CategoryProperty<>(categories.get(i), colors.get(i)));
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
		String categories = "Description [";
		for (CategoryProperty<CategoryType> categoryProperty : categoryProperties) {
			categories += categoryProperty.toString() + ",";
		}
		categories = categories.substring(0, categories.length() - 1);
		categories += "]";
		return categories;
	}

}
