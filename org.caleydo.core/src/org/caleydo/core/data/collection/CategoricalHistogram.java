/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.collection;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.util.color.Color;

/**
 * An implementation of {@link Histogram} for categorical data.
 *
 * @author Alexander Lex
 *
 */
public class CategoricalHistogram extends Histogram {
	private CategoricalClassDescription<?> classDescription;
	private int unkonwIndex = Integer.MAX_VALUE;

	/**
	 * @param numberOfBuckets
	 */
	public CategoricalHistogram(CategoricalClassDescription<?> classDescription) {
		super(classDescription.sizeWithoutUnknown());
		this.classDescription = classDescription;
		unkonwIndex = classDescription.indexOf(classDescription.getUnknownCategory().getCategory());
		if (unkonwIndex == -1)
			unkonwIndex = Integer.MAX_VALUE;
	}

	public void add(Object categoryType, Integer objectID) {

		if (categoryType.equals(classDescription.getUnknownCategory())) {
			addNAN(objectID);

		} else {
			int index = classDescription.indexOf(categoryType);
			if (index > unkonwIndex)
				index--;
			add(index, objectID);
		}
	}

	public Color getColor(int bucketNumber) {
		if (bucketNumber >= unkonwIndex)
			bucketNumber++;
		return classDescription.getCategoryProperties().get(bucketNumber).getColor();
	}

}
