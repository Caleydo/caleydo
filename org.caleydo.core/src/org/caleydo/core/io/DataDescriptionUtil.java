/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription.ECategoryType;
import org.caleydo.core.util.color.Color;

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

		for (List<String> row : dataMatrix) {
			for (String value : row) {
				categories.add(value);
			}
		}

		List<String> categoryValues = new ArrayList<>(categories);
		Collections.sort(categoryValues);

		CategoricalClassDescription<String> categoricalClassDescription = new CategoricalClassDescription<>();
		categoricalClassDescription.setCategoryType(ECategoryType.ORDINAL);
		categoricalClassDescription.setRawDataType(EDataType.STRING);

		for (String categoryValue : categoryValues) {
			categoricalClassDescription.addCategoryProperty(categoryValue, categoryValue, new Color("000000"));
		}

		return new DataDescription(EDataClass.CATEGORICAL, EDataType.STRING, categoricalClassDescription);
	}

	/**
	 * Creates a {@link DataDescription} for a specified column of the dataMatrix. It tries to automatically detect
	 * whether it is categorical or numerical data and fills the description accordingly.
	 *
	 * @param dataMatrix
	 *            Matrix containing data values. The outer list refers to rows.
	 * @param columnIndex
	 *            index of the column a {@link DataDescription} should be created for.
	 * @return
	 */
	public static DataDescription createDataDescription(List<List<String>> dataMatrix, int columnIndex) {

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
				continue;
			}
		}

		if (numNumbers >= dataMatrix.size() * 0.5f) {
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
		} else {
			List<String> categoryValues = new ArrayList<>(categories);
			Collections.sort(categoryValues);

			CategoricalClassDescription<String> categoricalClassDescription = new CategoricalClassDescription<>();
			categoricalClassDescription.setCategoryType(ECategoryType.ORDINAL);
			categoricalClassDescription.setRawDataType(EDataType.STRING);

			for (String categoryValue : categoryValues) {
				categoricalClassDescription.addCategoryProperty(categoryValue, categoryValue, new Color("000000"));
			}

			return new DataDescription(EDataClass.CATEGORICAL, EDataType.STRING, categoricalClassDescription);
		}
	}

}
