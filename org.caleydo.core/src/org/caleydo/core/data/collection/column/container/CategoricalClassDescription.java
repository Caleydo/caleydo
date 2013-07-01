/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
import org.caleydo.core.util.color.ColorBrewer;
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
 * Elements that are either not in a pre-defined category or that cause a parsing error are collected in a
 * {@link #unknownCategory}. This category is dynamically created, but you can also set it manually if you want to
 * influence e.g., its label or color.
 * </p>
 * <p>
 * See also {@link NumericalProperties}, this classes pendant for numerical data.
 * </p>
 *
 * @author Alexander Lex
 *
 */
@XmlType
public class CategoricalClassDescription<CATEGORY_TYPE extends Comparable<CATEGORY_TYPE>> implements
		Iterable<CategoryProperty<CATEGORY_TYPE>> {

	/** The type of the category */
	public enum ECategoryType {
		ORDINAL, NOMINAL;
	}

	/** The data type of the category. Defaults to Integer. */
	private EDataType rawDataType = EDataType.INTEGER;

	/** The category type of the category class. Defaults to ordinal */
	private ECategoryType categoryType = ECategoryType.ORDINAL;

	/** The properties of the categories that are part of this class */
	private List<CategoryProperty<CATEGORY_TYPE>> categoryProperties = new ArrayList<>();

	/** Category used for things that can't be parsed or that are not within the defined categories */
	private CategoryProperty<CATEGORY_TYPE> unknownCategory;

	@XmlTransient
	private HashMap<CATEGORY_TYPE, CategoryProperty<CATEGORY_TYPE>> hashCategoryToProperties = new HashMap<>();

	/**
	 *
	 */
	public CategoricalClassDescription() {
	}

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
	public void addCategoryProperty(CategoryProperty<CATEGORY_TYPE> categoryProperty) {
		categoryProperties.add(categoryProperty);
		hashCategoryToProperties.put(categoryProperty.getCategory(), categoryProperty);

	}

	/** Shorthand for {@link #addCategoryProperty(CategoryProperty)} */
	public void addCategoryProperty(CATEGORY_TYPE category, String categoryName, Color color) {
		CategoryProperty<CATEGORY_TYPE> property = new CategoryProperty<>(category, categoryName, color);
		addCategoryProperty(property);
	}

	/**
	 * @param categoryProperties
	 *            setter, see {@link categoryProperties}
	 */
	public void setCategoryProperties(List<CategoryProperty<CATEGORY_TYPE>> categoryProperties) {
		this.categoryProperties = categoryProperties;
		for (CategoryProperty<CATEGORY_TYPE> property : categoryProperties) {
			hashCategoryToProperties.put(property.getCategory(), property);
		}
	}

	/**
	 * @param unknownCategory
	 *            setter, see {@link unknownCategory}
	 */
	public void setUnknownCategory(CategoryProperty<CATEGORY_TYPE> unknownCategory) {
		this.unknownCategory = unknownCategory;
	}

	/**
	 * @return the unknownCategory, see {@link #unknownCategory}
	 */
	public CategoryProperty<CATEGORY_TYPE> getUnknownCategory() {
		return unknownCategory;
	}

	/**
	 * @return the categoryProperties, see {@link #categoryProperties}
	 */
	public List<CategoryProperty<CATEGORY_TYPE>> getCategoryProperties() {
		return categoryProperties;
	}

	@Override
	public Iterator<CategoryProperty<CATEGORY_TYPE>> iterator() {
		return categoryProperties.iterator();
	}

	/** Returns the number of categories including the unknown category. See also {@link #sizeWithoutUnknonw()} */
	public int size() {
		return categoryProperties.size();
	}

	/** Returns the number of categories excluding the unknown category. See also {@link #size()).*/
	public int sizeWithoutUnknonw() {
		return size() - 1;
	}

	/**
	 * <p>
	 * Initializes the Description as nominal with automatic qualitative colors. Sorts categories based on the
	 * comparable implementation of CategoryType.
	 * </p>
	 * <p>
	 * The number of categories auto initialization can handle is limited to the number of qualitative colors available.
	 * Typically this is around 10. More doesn't make sense.
	 * </p>
	 *
	 * @param unsortedCategories
	 */
	public void autoInitialize(Collection<CATEGORY_TYPE> unsortedCategories) {
		List<Color> colors = ColorManager.get().getColorList(ColorManager.QUALITATIVE_COLORS);

		if (unsortedCategories.size() > colors.size()) {
			throw new IllegalArgumentException(
					"Cannot auto-initialize more nominal categories than colors are available. " + unsortedCategories);
		}

		List<CATEGORY_TYPE> categories = new ArrayList<>(unsortedCategories);
		Collections.sort(categories);

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
	public CategoryProperty<CATEGORY_TYPE> getCategoryProperty(Object category) {
		return hashCategoryToProperties.get(category);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder("Description [");
		for (CategoryProperty<CATEGORY_TYPE> categoryProperty : categoryProperties) {
			b.append(categoryProperty).append(',');
		}
		b.setLength(b.length() - 1);
		b.append(']');
		return b.toString();
	}

	/**
	 * Applies a specified color scheme to the present categories.
	 *
	 * @param colorScheme
	 *            ColorScheme to apply.
	 * @param neutralCategory
	 *            If the color scheme is diverging, a neutral color is assigned to this category. May be null.
	 * @param reverseColorScheme
	 *            Determines whether the colors should be applied in reverse order.
	 */
	public void applyColorScheme(ColorBrewer colorScheme, CATEGORY_TYPE neutralCategory, boolean reverseColorScheme) {

		CategoryProperty<CATEGORY_TYPE> neutralCategoryProperty = hashCategoryToProperties.get(neutralCategory);

		int neutralColorIndex = -1;
		if (neutralCategoryProperty != null) {
			neutralColorIndex = categoryProperties.indexOf(neutralCategoryProperty);
		}

		List<Color> colors = colorScheme.getColors(categoryProperties.size(), neutralColorIndex, reverseColorScheme);

		for (int i = 0; i < categoryProperties.size(); i++) {
			CategoryProperty<CATEGORY_TYPE> categoryProperty = categoryProperties.get(i);
			categoryProperty.setColor(colors.get(i));
		}
	}
}
