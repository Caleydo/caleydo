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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.util.color.Colors;
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
public class CategoricalContainer<CATEGORY_TYPE extends Comparable<CATEGORY_TYPE>> implements IContainer<CATEGORY_TYPE> {

	public static final String UNKNOWN_CATEOGRY_STRING = "UNKN@WN";

	public static final Integer UNKNOWN_CATEGORY_INT = Integer.MIN_VALUE;

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
	private BiMap<CATEGORY_TYPE, Short> hashCategoryToIdentifier = HashBiMap.create();

	/** Maps every category to the number of elements it contains */
	private HashMap<CATEGORY_TYPE, Integer> hashCategoryToNumberOfMatches = new HashMap<>();

	private HashMap<Short, Float> hashCategoryKeyToNormalizedValue = new HashMap<>();

	/**
	 * Category for values where the category is not known, typically because of parsing errors or a missing definition
	 * in the file.
	 */
	private CATEGORY_TYPE unknownCategoryType;

	/**
	 * An ordered list of categories for this container. Can either be set using
	 * {@link #setPossibleCategories(ArrayList)} to include categories which are not in the dataset itself, or is set
	 * automatically once {@link #normalize()} is called.
	 */
	private CategoricalClassDescription<CATEGORY_TYPE> categoricalClassDescription;

	/**
	 * Initializes the container.
	 *
	 * @param size
	 * @param dataType
	 * @param unknownCategoryType
	 *            The value used for the unknown value. Use {@link #UNKNOWN_CATEGORY_INT} and
	 *            {@link #UNKNOWN_CATEOGRY_STRING} unless there is a good reason not to do so.
	 */
	public CategoricalContainer(int size, EDataType dataType, CATEGORY_TYPE unknownCategoryType) {
		container = new short[size];
		this.unknownCategoryType = unknownCategoryType;
		initCategory(unknownCategoryType);
	}

	/**
	 * Appends the category to the container.
	 *
	 * @param index
	 * @param category
	 */
	@Override
	public void add(CATEGORY_TYPE category) throws IndexOutOfBoundsException {
		if (nextIndex == container.length) {
			throw new IndexOutOfBoundsException(nextIndex + " - cannot add " + category);
		}
		Short identifier = hashCategoryToIdentifier.get(category);
		if (identifier == null) {
			if (categoricalClassDescription != null) {
				// we have encountered a category which is not available in the class description
				Logger.log(new Status(IStatus.WARNING, this.toString(), "No category for " + category
						+ " in description " + categoricalClassDescription));
				identifier = hashCategoryToIdentifier.get(unknownCategoryType);
				category = unknownCategoryType;

			} else {
				identifier = initCategory(category);
			}
		}
		container[nextIndex++] = identifier;
		Integer numberOfMatches = hashCategoryToNumberOfMatches.get(category);
		hashCategoryToNumberOfMatches.put(category, numberOfMatches);
	}

	@Override
	public void addUnknown() {
		add(unknownCategoryType);
		if (categoricalClassDescription.getUnknownCategory() == null) {
			categoricalClassDescription.setUnknownCategory(new CategoryProperty<CATEGORY_TYPE>(unknownCategoryType,
					"Unknown", Colors.NOT_A_NUMBER_COLOR));
		}
	}

	@Override
	public boolean isUnknown(CATEGORY_TYPE value) {
		return value == unknownCategoryType;
	}

	/**
	 * Adds a new category - this should only happen if no {@link CategoricalClassDescription} exist
	 *
	 * @param category
	 * @return
	 */
	private short initCategory(CATEGORY_TYPE category) {
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

	@Override
	public Iterator<CATEGORY_TYPE> iterator() {
		return new ContainerIterator<>(this);
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
	public CATEGORY_TYPE get(int index) {
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

		if (categoricalClassDescription == null) {
			categoricalClassDescription = new CategoricalClassDescription<>();
			categoricalClassDescription.autoInitialize(hashCategoryToIdentifier.keySet());
		}

		if (categoricalClassDescription.size() == 0)
			throw new IllegalStateException("Can't normalize an empty categorical container");

		float normalizedDistance = 0;

		if (categoricalClassDescription.size() > 1) {
			int numCategories = categoricalClassDescription.size() - 1;
			if (categoricalClassDescription.getCategoryProperty(unknownCategoryType) != null) {
				numCategories--;
			}
			normalizedDistance = 1f / numCategories;
		}

		for (int i = 0; i < categoricalClassDescription.size(); i++) {
			List<CategoryProperty<CATEGORY_TYPE>> c = categoricalClassDescription.getCategoryProperties();
			short key = hashCategoryToIdentifier.get(c.get(i).getCategory());
			hashCategoryKeyToNormalizedValue.put(key, i * normalizedDistance);
		}

		short key = hashCategoryToIdentifier.get(unknownCategoryType);
		hashCategoryKeyToNormalizedValue.put(key, Float.NaN);

		float[] target = new float[container.length];

		for (int count = 0; count < container.length; count++) {
			Short categoryID = container[count];
			if (hashCategoryKeyToNormalizedValue.containsKey(categoryID)) {
				float normalized = hashCategoryKeyToNormalizedValue.get(categoryID);
				target[count] = normalized;
			} else {
				throw new IllegalStateException("Unknown category ID: " + categoryID);
			}
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
	public void setCategoryDescritions(CategoricalClassDescription<CATEGORY_TYPE> categoryDescriptions) {
		this.categoricalClassDescription = categoryDescriptions;
		for (CategoryProperty<CATEGORY_TYPE> category : categoryDescriptions) {
			initCategory(category.getCategory());
		}
	}

	/**
	 * @return the categoricalClassDescription, see {@link #categoricalClassDescription}
	 */
	public CategoricalClassDescription<CATEGORY_TYPE> getCategoryDescriptions() {
		return categoricalClassDescription;
	}

	@Override
	public EDataType getDataType() {
		return dataType;
	}

	/** Returns a set of all registered categories */
	public Set<CATEGORY_TYPE> getCategories() {
		return hashCategoryToIdentifier.keySet();
	}

}
