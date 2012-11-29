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
import java.util.Collections;
import java.util.Iterator;

import org.caleydo.view.tourguide.data.ScoringElement;

/**
 * @author Samuel Gratzl
 *
 */
public class CombinedScore implements ICompositeScore {
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

	@Override
	public int size() {
		return children.size();
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public final EScoreType getScoreType() {
		return EScoreType.STANDALONE_SCORE;
	}

	@Override
	public float getScore(ScoringElement elem) {
		float[] data = new float[children.size()];
		int i = 0;
		for (IScore child : children)
			data[i++] = child.getScore(elem);
		return operator.combine(data);
	}



}
