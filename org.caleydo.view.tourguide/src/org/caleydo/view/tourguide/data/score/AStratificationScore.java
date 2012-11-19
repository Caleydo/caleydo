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

import java.util.HashMap;
import java.util.Map;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.view.tourguide.data.ScoringElement;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AStratificationScore implements IScore {
	protected TablePerspective reference;
	protected Map<Integer, Float> scores = new HashMap<>();

	public AStratificationScore() {

	}

	public AStratificationScore(TablePerspective reference) {
		this.reference = reference;
	}

	protected boolean contains(TablePerspective elem) {
		// have in cache or the same
		return scores.containsKey(elem.getID()) || reference.equals(elem);
	}

	protected void put(TablePerspective elem, float value) {
		scores.put(elem.getID(), value);
	}

	public TablePerspective getReference() {
		return reference;
	}

	@Override
	public final boolean isGroupScore() {
		return false;
	}

	@Override
	public String getLabel() {
		return reference.getLabel();
	}

	@Override
	public String getProviderName() {
		return reference.getProviderName();
	}

	@Override
	public float getScore(ScoringElement elem) {
		TablePerspective p = elem.getStratification();
		Float f = scores.get(p.getID());
		return f == null ? Float.NaN : f.floatValue();
	}


	@Override
	public String getRepr(ScoringElement elem) {
		float f = getScore(elem);
		return Float.isNaN(f) ? "" : Formatter.formatNumber(f);
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
		result = prime * result + ((reference == null) ? 0 : reference.hashCode());
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
		AStratificationScore other = (AStratificationScore) obj;
		if (reference == null) {
			if (other.reference != null)
				return false;
		} else if (!reference.equals(other.reference))
			return false;
		return true;
	}

}