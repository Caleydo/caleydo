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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.view.tourguide.data.ScoringElement;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AGroupScore extends DefaultLabelProvider implements IGroupScore {
	protected TablePerspective stratification;
	protected Group group;
	protected Map<Integer, Float> scores = new ConcurrentHashMap<>();

	public AGroupScore() {
		super("");
	}

	public AGroupScore(String label, TablePerspective stratification, Group group) {
		super(label == null ? stratification.getRecordPerspective().getLabel() + ": " + group.getLabel() : label);
		this.stratification = stratification;
		this.group = group;
		put(this.group, Float.NaN); // add self
	}


	public boolean contains(TablePerspective perspective, Group elem) {
		// have the value or it the same stratification
		return scores.containsKey(elem.getID()) || (perspective.equals(stratification));
	}


	public void put(Group elem, float value) {
		scores.put(elem.getID(), value);
	}

	@Override
	public String getProviderName() {
		return stratification.getLabel();
	}

	/**
	 * @return the stratification
	 */
	@Override
	public TablePerspective getStratification() {
		return stratification;
	}

	/**
	 * @return the group
	 */
	@Override
	public Group getGroup() {
		return group;
	}

	@Override
	public final EScoreType getScoreType() {
		return EScoreType.GROUP_SCORE;
	}

	@Override
	public float getScore(ScoringElement elem) {
		if (elem.getGroup() == null)
			return Float.NaN;
		Float f = scores.get(elem.getGroup().getID());
		return f == null ? Float.NaN : f.floatValue();
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
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + ((stratification == null) ? 0 : stratification.hashCode());
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
		AGroupScore other = (AGroupScore) obj;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		if (stratification == null) {
			if (other.stratification != null)
				return false;
		} else if (!stratification.equals(other.stratification))
			return false;
		return true;
	}

}