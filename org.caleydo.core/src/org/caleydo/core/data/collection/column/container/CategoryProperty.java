/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.collection.column.container;

import org.caleydo.core.util.color.Color;

/**
 * @author Alexander Lex
 *
 */
public class CategoryProperty<CATEGORY_TYPE> {


	/** The unique identifier of the category as found in the source data file */
	private CATEGORY_TYPE category;

	/** An optional string representation of the category */
	private String categoryName;

	/** The color used to represent the category */
	private Color color;

	/**
	 *
	 */
	public CategoryProperty() {
	}

	/**
	 * Constructor that sets {@link #categoryName} to category.toString()
	 *
	 * @param category
	 * @param color
	 */
	public CategoryProperty(CATEGORY_TYPE category, Color color) {
		this.category = category;
		this.color = color;
		this.categoryName = category.toString();
	}

	public CategoryProperty(CATEGORY_TYPE category, String categoryName, Color color) {
		this.category = category;
		this.color = color;
		this.categoryName = categoryName;
	}

	// @Override
	// public int compareTo(CategoryType comparedIdentifier) {
	// return category.compareTo(comparedIdentifier);
	// }

	/**
	 * @param category
	 *            setter, see {@link category}
	 */
	public void setCategory(CATEGORY_TYPE category) {
		this.category = category;
	}

	/**
	 * @return the category, see {@link #category}
	 */
	public CATEGORY_TYPE getCategory() {
		return category;
	}

	/**
	 * @param categoryName
	 *            setter, see {@link categoryName}
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	/**
	 * @return the categoryName, see {@link #categoryName}
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * @param color
	 *            setter, see {@link color}
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return the color, see {@link #color}
	 */
	public Color getColor() {
		return color;
	}

	@Override
	public String toString() {
		return categoryName;
	}

}
