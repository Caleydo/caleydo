/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import java.util.EnumSet;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription.ECategoryType;
import org.caleydo.core.io.NumericalProperties;

import com.google.common.base.Predicate;

/**
 * Validators that determine whether a selected cell is valid.
 *
 * @author Christian
 *
 */
public final class CellSelectionValidators {
	private CellSelectionValidators() {
	}

	/**
	 * Creates a validator for cells that must have a minimum amount of elements.
	 *
	 * @param minElements
	 * @return
	 */
	public static Predicate<DataCellInfo> minElementsValidator(final int minElements) {
		return new Predicate<DataCellInfo>() {

			@Override
			public boolean apply(DataCellInfo info) {
				return info.columnPerspective.getVirtualArray().size() > minElements;
			}
		};
	}

	/**
	 * Creates a validator for cells that must have at least 1 element.
	 *
	 * @return
	 */
	public static Predicate<DataCellInfo> nonEmptyCellValidator() {
		return minElementsValidator(1);
	}

	/**
	 * Creates a validator that only permits numerical data cells.
	 *
	 * @return
	 */
	public static Predicate<DataCellInfo> numericalValuesValidator() {
		return new Predicate<DataCellInfo>() {

			@Override
			public boolean apply(DataCellInfo info) {

				Object description = info.dataDomain.getDataClassSpecificDescription(info.rowIDType, info.rowID,
						info.columnPerspective.getIdType(), info.columnPerspective.getVirtualArray().get(0));

				return description == null || description instanceof NumericalProperties;
			}
		};
	}

	/**
	 * Creates a validator that only permits categorical data cells with the specified {@link ECategoryType}s.
	 *
	 * @param categories
	 * @return
	 */
	public static Predicate<DataCellInfo> categoricalValuesValidator(final EnumSet<ECategoryType> categories) {
		return new Predicate<DataCellInfo>() {

			@Override
			public boolean apply(DataCellInfo info) {

				Object description = info.dataDomain.getDataClassSpecificDescription(info.rowIDType, info.rowID,
						info.columnPerspective.getIdType(), info.columnPerspective.getVirtualArray().get(0));
				if (description == null || description instanceof NumericalProperties)
					return false;
				CategoricalClassDescription<?> desc = (CategoricalClassDescription<?>) description;
				return categories.contains(desc.getCategoryType());
			}
		};
	}

}
