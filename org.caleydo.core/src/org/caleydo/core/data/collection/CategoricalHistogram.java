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
