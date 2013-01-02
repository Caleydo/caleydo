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
package org.caleydo.view.tourguide.api.score;

import java.util.Collection;
import java.util.Iterator;

import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.query.ScoringElement;
import org.caleydo.view.tourguide.spi.compute.ICompositeScore;
import org.caleydo.view.tourguide.spi.score.IScore;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
public class CombinedScore extends DefaultLabelProvider implements ICompositeScore {
	private final ECombinedOperator operator;
	// score,weight
	private final Collection<TransformedScore> children;

	public CombinedScore(String label, ECombinedOperator op, Collection<TransformedScore> children) {
		super(label);
		this.operator = op;
		this.children = children;
	}

	public static Collection<TransformedScore> wrap(Collection<IScore> scores) {
		return Lists.newArrayList(Collections2.transform(scores, new Function<IScore, TransformedScore>() {
			@Override
			public TransformedScore apply(IScore s) {
				return new TransformedScore(s);
			}
		}));
	}

	@Override
	public void onRegistered() {

	}

	@Override
	public boolean supports(EDataDomainQueryMode mode) {
		for (TransformedScore child : children)
			if (!child.getScore().supports(mode))
				return false;
		return true;
	}

	/**
	 * @return the operator
	 */
	public ECombinedOperator getOperator() {
		return operator;
	}

	public static final Function<TransformedScore, IScore> retrieveScore = new Function<TransformedScore, IScore>() {
		@Override
		public IScore apply(TransformedScore tscore) {
			return tscore.getScore();
		}
	};

	@Override
	public Iterator<IScore> iterator() {
		return Iterators.transform(children.iterator(), retrieveScore);
	}

	@Override
	public Collection<IScore> getChildren() {
		return Collections2.transform(children, retrieveScore);
	}

	@Override
	public void mapChildren(final Function<IScore, IScore> f) {
		for (TransformedScore in : this.children)
			in.setScore(f.apply(in.getScore()));
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
	public String getAbbreviation() {
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
		for (TransformedScore child : children)
			data[i++] = child.getScore(elem);
		return operator.combine(data);
	}


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

	public static class TransformedScore {
		private IScore score;
		private float factor = 1.0f;
		private float powerof = 1.0f;
		private float shift = 0.0f;

		public TransformedScore(IScore score) {
			this.score = score;
		}


		public float getScore(ScoringElement elem) {
			float f = score.getScore(elem);
			if (Float.isNaN(f))
				return f;
			return (float) (factor * Math.pow(f, powerof) + shift);
		}

		/**
		 * @param score
		 *            setter, see {@link score}
		 */
		public void setScore(IScore score) {
			this.score = score;
		}

		/**
		 * @return the factor, see {@link #factor}
		 */
		public float getFactor() {
			return factor;
		}

		/**
		 * @param factor
		 *            setter, see {@link factor}
		 */
		public void setFactor(float factor) {
			this.factor = factor;
		}

		/**
		 * @return the powerof, see {@link #powerof}
		 */
		public float getPowerof() {
			return powerof;
		}

		/**
		 * @param powerof
		 *            setter, see {@link powerof}
		 */
		public void setPowerof(float powerof) {
			this.powerof = powerof;
		}

		/**
		 * @return the shift, see {@link #shift}
		 */
		public float getShift() {
			return shift;
		}

		/**
		 * @param shift
		 *            setter, see {@link shift}
		 */
		public void setShift(float shift) {
			this.shift = shift;
		}

		/**
		 * @return the score, see {@link #score}
		 */
		public IScore getScore() {
			return score;
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
			TransformedScore other = (TransformedScore) obj;
			if (score == null) {
				if (other.score != null)
					return false;
			} else if (!score.equals(other.score))
				return false;
			return true;
		}


	}
}
