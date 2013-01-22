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
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * <p>
 * Container implementation for categorical data. Categories are identified through the <code>CategoryType</code>.
 * </p>
 * <p>
 * For storage optimization we don't store the <code>CategoryType</code> objects for every category but build a map of
 * categories and use shorts as keys.
 * </p>
 *
 * @author Alexander Lex
 */
public class CategoricalContainer<CategoryType extends Comparable<CategoryType>> implements IContainer<CategoryType> {

	/** The data type corresponding to CategoryType */
	private EDataType dataType;

	/**
	 * This contains the short that is used for the next available key. This also corresponds to the number of
	 * categories.
	 */
	private short nextAvailableKey = 0;
	/** Keeps track of the next free index for adding data */
	private int nextIndex = 0;

	/** The array that holds the mapped keys as they appear in the input files */
	private short[] container;

	/** BiMap that maps category names to identifiers. */
	private BiMap<CategoryType, Short> hashCategoryToIdentifier = HashBiMap.create();

	/** Maps every category to the number of elements it contains */
	private HashMap<CategoryType, Integer> hashCategoryToNumberOfMatches = new HashMap<>();

	private HashMap<Short, Float> hashCategoryKeyToNormalizedValue = new HashMap<>();

	/**
	 * An ordered list of categories for this container. Can either be set using
	 * {@link #setPossibleCategories(ArrayList)} to include categories which are not in the dataset itself, or is set
	 * automatically once {@link #normalize()} is called.
	 */
	private CategoryDescriptions<CategoryType> categoryDescriptions;

	public CategoricalContainer(int size, EDataType dataType) {
		container = new short[size];
	}

	/**
	 * Adds the value of categoryName to the container at position index
	 *
	 * @param index
	 * @param categoryName
	 */
	@Override
	public void add(CategoryType categoryName) {
		Short identifier = hashCategoryToIdentifier.get(categoryName);
		if (identifier == null) {
			identifier = initCategory(categoryName);
		}
		container[nextIndex++] = identifier;
		Integer numberOfMatches = hashCategoryToNumberOfMatches.get(categoryName);
		hashCategoryToNumberOfMatches.put(categoryName, numberOfMatches);
	}

	/**
	 * Adds a new category - this should only happen if no {@link CategoryDescriptions} exist
	 *
	 * @param category
	 * @return
	 */
	private short initCategory(CategoryType category) {
		if (hashCategoryToIdentifier.containsKey(category))
			return hashCategoryToIdentifier.get(category);

		short identifier = nextAvailableKey++;
		hashCategoryToIdentifier.put(category, identifier);
		// initializing the key
		hashCategoryToNumberOfMatches.put(category, 0);
		return identifier;
	}

	@Override
	public int size() {
		return container.length;
	}

	/**
	 * Returns the value associated with the index at the variable
	 *
	 * @throws IndexOutOfBoundsException
	 *             if index is out of specified range
	 * @param index
	 *            the index of the variable
	 * @return the variable associated with the index or null if no such index exists
	 */
	@Override
	public CategoryType get(int index) {
		try {
			return hashCategoryToIdentifier.inverse().get(container[index]);
		} catch (NullPointerException npe) {
			Logger.log(new Status(IStatus.ERROR, this.toString(), "Caught npe on accessing container with index: "
					+ index, npe));
			return null;
		}
	}

	/**
	 * Converting the categories into quantitative space (0-1)
	 */
	@Override
	public FloatContainer normalize() {

		if (categoryDescriptions == null) {
			categoryDescriptions = new CategoryDescriptions<>();
			categoryDescriptions.autoInitialize(hashCategoryToIdentifier.keySet());
		}

		if (categoryDescriptions.size() == 0)
			throw new IllegalStateException("Can't normalize an empty categorical container");

		float normalizedDistance = 0;

		if (categoryDescriptions.size() > 1) {
			normalizedDistance = 1f / (categoryDescriptions.size() - 1);
		}

		for (int i = 0; i < categoryDescriptions.size(); i++) {
			List<CategoryProperty<CategoryType>> c = categoryDescriptions.getCategoryProperties();

			short key = hashCategoryToIdentifier.get(c.get(i).getCategory());
			hashCategoryKeyToNormalizedValue.put(key, i * normalizedDistance);
		}

		float[] target = new float[container.length];

		for (int count = 0; count < container.length; count++) {
			short categoryID = container[count];
			float normalized = hashCategoryKeyToNormalizedValue.get(categoryID);
			target[count] = normalized;
		}
		// System.out.println("Elapsed: " + (System.currentTimeMillis() - startTime));
		return new FloatContainer(target);
	}

	/**
	 * Provide a list with all possible categories. Useful when the data set does not contain all values by itself. Take
	 * care that every value in the data set is also in this list, otherwise an exception will occur
	 *
	 * @param possibleCategories
	 *            the List
	 */
	public void setCategoryDescritions(CategoryDescriptions<CategoryType> categoryDescriptions) {
		this.categoryDescriptions = categoryDescriptions;
		for (CategoryProperty<CategoryType> category : categoryDescriptions) {
			initCategory(category.getCategory());
		}
	}

	/**
	 * @return the categoryDescriptions, see {@link #categoryDescriptions}
	 */
	public CategoryDescriptions<CategoryType> getCategoryDescriptions() {
		return categoryDescriptions;
	}

	@Override
	public EDataType getDataType() {
		return dataType;
	}

	/** Returns a set of all registered categories */
	public Set<CategoryType> getCategories() {
		return hashCategoryToIdentifier.keySet();
	}

}
