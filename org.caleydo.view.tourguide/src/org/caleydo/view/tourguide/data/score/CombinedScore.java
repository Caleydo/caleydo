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
package org.caleydo.view.tourguide.data.score;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.caleydo.core.util.format.Formatter;
import org.caleydo.view.tourguide.data.ScoringElement;

/**
 * @author Samuel Gratzl
 *
 */
public class CombinedScore implements ICompositeScore {
	public enum ECombinedOperator {
		MAX, MIN, MEAN, MEDIAN, PRODUCT, GEOMETRIC_MEAN
	}

	private final String label;
	private final ECombinedOperator operator;
	private final Collection<IScore> children;

	public CombinedScore(String label, ECombinedOperator op, Collection<IScore> children) {
		this.label = label;
		this.operator = op;
		this.children = children;
	}

	@Override
	public String getProviderName() {
		return null;
	}
	/**
	 * @return the operator
	 */
	public ECombinedOperator getOperator() {
		return operator;
	}

	@Override
	public Iterator<IScore> iterator() {
		return children.iterator();
	}

	@Override
	public Collection<IScore> getChildren() {
		return Collections.unmodifiableCollection(children);
	}

	public int size() {
		return children.size();
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public boolean isGroupScore() {
		for (IScore child : this)
			if (child.isGroupScore())
				return true;
		return false;
	}

	@Override
	public float getScore(ScoringElement elem) {
		float[] data = new float[children.size()];
		int i = 0;
		for (IScore child : children)
			data[i++] = child.getScore(elem);
		return combine(data);
	}

	@Override
	public String getRepr(ScoringElement elem) {
		float f = getScore(elem);
		return Float.isNaN(f) ? "" : Formatter.formatNumber(f);
	}

	/**
	 * @param data
	 * @return
	 */
	private float combine(float[] data) {
		float c = 0;
		switch (operator) {
		case MAX:
			if (data.length == 0)
				return Float.NaN;
			c = data[0];
			for (int i = 1; i < data.length; ++i)
				c = Math.max(c, data[i]);
			return c;
		case MIN:
			if (data.length == 0)
				return Float.NaN;
			c = data[0];
			for (int i = 1; i < data.length; ++i)
				c = Math.min(c, data[i]);
			return c;
		case MEAN:
			if (data.length == 0)
				return 0;
			c = 0;
			for (int i = 0; i < data.length; ++i)
				c += data[i];
			return c / data.length;
		case PRODUCT:
			if (data.length == 0)
				return 1;
			c = 1;
			for (int i = 0; i < data.length; ++i)
				c *= data[i];
			return c;
		case GEOMETRIC_MEAN:
			if (data.length == 0)
				return 1;
			c = 1;
			for (int i = 0; i < data.length; ++i)
				c *= data[i];
			return (float) Math.pow(c, 1. / data.length);
		case MEDIAN:
			Arrays.sort(data);
			int center = data.length / 2;
			if (data.length % 2 == 0)
				return 0.5f * (data[center] + data[center + 1]);
			else
				return data[center + 1];
		}
		throw new IllegalStateException("unknown operator: " + operator);
	}

}
