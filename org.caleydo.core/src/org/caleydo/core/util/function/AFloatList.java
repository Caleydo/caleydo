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
package org.caleydo.core.util.function;

import java.util.AbstractList;

/**
 * basic implementation of a {@link IFloatList}
 *
 * @author Samuel Gratzl
 *
 */
public abstract class AFloatList extends AbstractList<Float> implements IFloatList {

	@Override
	public final IFloatListView map(IFloatFunction f) {
		return new TransformedFloatListView(this, f);
	}

	@Override
	public final Float get(int index) {
		return getPrimitive(index);
	}

	@Override
	public final float[] computeStats() {
		float min = Float.POSITIVE_INFINITY;
		float max = Float.NEGATIVE_INFINITY;
		boolean any = false;
		int s = size();
		for (int i = 0; i < s; ++i) {
			float v = get(i);
			if (Float.isNaN(v))
				continue;
			if (v < min)
				min = v;
			if (max < v)
				max = v;
			any = true;
		}
		if (!any)
			return new float[] { Float.NaN, Float.NaN };
		return new float[] { min, max };
	}


}
