/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription.ECategoryType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorManager;

/**
 * Utility class to create default {@link DataDescription} objects from specified data.
 *
 * @author Christian Partl
 *
 */
public final class DataDescriptionUtil {

	private DataDescriptionUtil() {
	}

	/**
	 * Creates a numerical {@link DataDescription} object and fills default values according to the provided data
	 * matrix.
	 *
	 * @param dataMatrix
	 *            Matrix containing data values. The outer list refers to rows.
	 * @return
	 */
	public static DataDescription createNumericalDataDescription(List<List<String>> dataMatrix) {

		float min = Float.POSITIVE_INFINITY;
		float max = Float.NEGATIVE_INFINITY;
		boolean useIntegers = true;
		boolean maxDefined = false;
		boolean minDefined = false;

		for (List<String> row : dataMatrix) {
			for (String value : row) {
				float val = 0;
				try {
					val = Float.parseFloat(value);
					if (val < min) {
						min = val;
						minDefined = true;
					}
					if (val > max) {
						max = val;
						maxDefined = true;
					}
					if (useIntegers) {
						try {
							Integer.parseInt(value);
						} catch (NumberFormatException e) {
							useIntegers = false;
							continue;
						}
					}
				} catch (NumberFormatException e) {
					continue;
				}
			}
		}

		NumericalProperties numericalProperties = new NumericalProperties();

		if (max > 0 && min < 0) {
			numericalProperties.setDataCenter(0d);
		}
		if (minDefined)
			numericalProperties.setMin(min);
		if (maxDefined)
			numericalProperties.setMax(max);

		return new DataDescription(useIntegers ? EDataClass.NATURAL_NUMBER : EDataClass.REAL_NUMBER,
				useIntegers ? EDataType.INTEGER : EDataType.FLOAT, numericalProperties);

	}

	/**
	 * Creates a categorical {@link DataDescription} object and fills default values according to the provided data
	 * matrix.
	 *
	 * @param dataMatrix
	 *            Matrix containing data values. The outer list refers to rows.
	 * @return
	 */
	public static DataDescription createCategoricalDataDescription(List<List<String>> dataMatrix) {

		Set<String> categories = new HashSet<>();
		boolean isIntCategory = true;

		for (List<String> row : dataMatrix) {
			for (String value : row) {
				categories.add(value);
				try {
					Integer.parseInt(value);
				} catch (NumberFormatException e) {
					isIntCategory = false;
				}
			}
		}

		List<String> categoryValues = new ArrayList<>(categories);
		Collections.sort(categoryValues);

		CategoricalClassDescription<String> categoricalClassDescription = new CategoricalClassDescription<>();
		categoricalClassDescription.setCategoryType(isIntCategory ? ECategoryType.ORDINAL : ECategoryType.NOMINAL);
		categoricalClassDescription.setRawDataType(EDataType.STRING);

		for (String categoryValue : categoryValues) {
			categoricalClassDescription.addCategoryProperty(categoryValue, categoryValue, new Color("000000"));
		}

		return new DataDescription(EDataClass.CATEGORICAL, EDataType.STRING, categoricalClassDescription);
	}

	public static DataDescription createCategoricalDataDescription(List<List<String>> dataMatrix, int columnIndex) {
		return createDataDescription(dataMatrix, columnIndex, true, false);
	}

	private static DataDescription createDataDescription(List<List<String>> dataMatrix, int columnIndex,
			boolean categoricalOnly, boolean numericalOnly) {
		Set<String> categories = new HashSet<>();
		float min = Float.POSITIVE_INFINITY;
		float max = Float.NEGATIVE_INFINITY;
		int numNumbers = 0;
		boolean useIntegers = true;
		boolean maxDefined = false;
		boolean minDefined = false;

		for (List<String> row : dataMatrix) {
			String value = row.get(columnIndex);
			categories.add(value);
			float val = 0;

			try {
				val = Float.parseFloat(value);
				numNumbers++;
				if (val < min) {
					min = val;
					minDefined = true;
				}
				if (val > max) {
					max = val;
					maxDefined = true;
				}
				if (useIntegers) {
					try {
						Integer.parseInt(value);
					} catch (NumberFormatException e) {
						useIntegers = false;
						continue;
					}
				}
			} catch (NumberFormatException e) {
				useIntegers = false;
				continue;
			}
		}

		if (!categoricalOnly && (numNumbers >= dataMatrix.size() * 0.5f || numericalOnly)) {
			NumericalProperties numericalProperties = new NumericalProperties();

			if (max > 0 && min < 0) {
				numericalProperties.setDataCenter(0d);
			}
			if (minDefined)
				numericalProperties.setMin(min);
			if (maxDefined)
				numericalProperties.setMax(max);

			return new DataDescription(useIntegers ? EDataClass.NATURAL_NUMBER : EDataClass.REAL_NUMBER,
					useIntegers ? EDataType.INTEGER : EDataType.FLOAT, numericalProperties);
		}

		List<String> categoryValues = new ArrayList<>(categories);
		Collections.sort(categoryValues);

		CategoricalClassDescription<String> categoricalClassDescription = new CategoricalClassDescription<>();
		categoricalClassDescription.setCategoryType(useIntegers ? ECategoryType.ORDINAL : ECategoryType.NOMINAL);
		categoricalClassDescription.setRawDataType(EDataType.STRING);

		Iterator<Color> colorIterator = ColorManager.get().getColorList(ColorManager.QUALITATIVE_COLORS).iterator();

		for (String categoryValue : categoryValues) {
			if (!colorIterator.hasNext()) {
				colorIterator = ColorManager.get().getColorList(ColorManager.QUALITATIVE_COLORS).iterator();
			}
			categoricalClassDescription.addCategoryProperty(categoryValue, categoryValue, colorIterator.next());
		}

		return new DataDescription(EDataClass.CATEGORICAL, EDataType.STRING, categoricalClassDescription);

	}

	/**
	 * Creates a {@link DataDescription} for a specified column of the dataMatrix. It tries to automatically detect
	 * whether it is categorical or numerical data and fills the description accordingly. The data is considered
	 * numerical, if most data points are floats or ints.
	 *
	 * @param dataMatrix
	 *            Matrix containing data values. The outer list refers to rows.
	 * @param columnIndex
	 *            index of the column a {@link DataDescription} should be created for.
	 * @return
	 */
	public static DataDescription createDataDescription(List<List<String>> dataMatrix, int columnIndex) {
		return createDataDescription(dataMatrix, columnIndex, false, false);
	}

}
