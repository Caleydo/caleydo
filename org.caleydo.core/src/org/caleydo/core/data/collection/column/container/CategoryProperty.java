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
