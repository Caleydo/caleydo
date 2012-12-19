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

import java.util.Collection;
import java.util.Iterator;

import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.tourguide.data.ScoringElement;
import org.caleydo.view.tourguide.data.compute.ICompositeScore;

import com.google.common.collect.Collections2;
import com.google.common.collect.Iterators;

/**
 * @author Samuel Gratzl
 *
 */
public class CombinedScore extends DefaultLabelProvider implements ICompositeScore {
	private final ECombinedOperator operator;
	// score,weight
	private final Collection<Pair<IScore, Float>> children;

	public CombinedScore(String label, ECombinedOperator op, Collection<Pair<IScore, Float>> children) {
		super(label);
		this.operator = op;
		this.children = children;
	}

	/**
	 * @return the operator
	 */
	public ECombinedOperator getOperator() {
		return operator;
	}

	@Override
	public Iterator<IScore> iterator() {
		return Iterators.transform(children.iterator(), Pair.<IScore, Float> mapFirst());
	}

	@Override
	public Collection<IScore> getChildren() {
		return Collections2.transform(children, Pair.<IScore, Float> mapFirst());
	}

	@Override
	public int size() {
		return children.size();
	}

	@Override
	public final EScoreType getScoreType() {
		return EScoreType.STRATIFICATION_SCORE;
	}

	@Override
	public String getAbbrevation() {
		switch (this.operator) {
		case GEOMETRIC_MEAN:
			return "GEO";
		case MAX:
			return "MAX";
		case MEAN:
			return "AVG";
		case MEDIAN:
			return "MED";
		case MIN:
			return "MIN";
		case PRODUCT:
			return "PRO";
		}
		return "CB";
	}

	@Override
	public float getScore(ScoringElement elem) {
		float[] data = new float[children.size()];
		int i = 0;
		for (Pair<IScore, Float> child : children)
			data[i++] = child.getFirst().getScore(elem) * child.getSecond();
		return operator.combine(data);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((operator == null) ? 0 : operator.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CombinedScore other = (CombinedScore) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (operator != other.operator)
			return false;
		return true;
	}
}
