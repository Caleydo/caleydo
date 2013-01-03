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
package org.caleydo.view.tourguide.api.query.filter;

import org.caleydo.view.tourguide.api.query.ScoringElement;
import org.caleydo.view.tourguide.spi.query.filter.IScoreFilter;
import org.caleydo.view.tourguide.spi.score.IScore;

/**
 * simple compare filter
 *
 * @author Samuel Gratzl
 *
 */
public final class CompareScoreFilter implements IScoreFilter {

	private final IScore score;
	private ECompareOperator op;
	private float against;

	public CompareScoreFilter(IScore score, ECompareOperator op, float against) {
		this.score = score;
		this.op = op;
		this.against = against;
	}

	@Override
	public IScore getReference() {
		return score;
	}

	/**
	 * @return the against, see {@link #against}
	 */
	public float getAgainst() {
		return against;
	}

	/**
	 * @param against
	 *            the against to set
	 */
	public void setAgainst(float against) {
		this.against = against;
	}

	/**
	 * @return the op, see {@link #op}
	 */
	public ECompareOperator getOp() {
		return op;
	}

	/**
	 * @param op
	 *            the op to set
	 */
	public void setOp(ECompareOperator op) {
		this.op = op;
	}

	@Override
	public boolean apply(ScoringElement elem) {
		float f = score.getScore(elem);
		return op.apply(f, this.against);
	}

	@Override
	public CompareScoreFilter clone() {
		try {
			return (CompareScoreFilter) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
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
		result = prime * result + Float.floatToIntBits(against);
		result = prime * result + ((op == null) ? 0 : op.hashCode());
		result = prime * result + ((score == null) ? 0 : score.hashCode());
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
		CompareScoreFilter other = (CompareScoreFilter) obj;
		if (Float.floatToIntBits(against) != Float.floatToIntBits(other.against))
			return false;
		if (op != other.op)
			return false;
		if (score == null) {
			if (other.score != null)
				return false;
		} else if (!score.equals(other.score))
			return false;
		return true;
	}


}