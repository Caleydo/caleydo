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

import java.util.Objects;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AGroupScore extends AComputedGroupScore implements IGroupScore {
	protected TablePerspective stratification;
	protected Group group;
	/**
	 * if true, add another filter that will produce NaN if the referred groups are equal in the the same categorical
	 * datadomain
	 */
	protected boolean mutualExclusive;

	public AGroupScore() {
		super("");
	}

	public AGroupScore(String label, TablePerspective stratification, Group group, boolean mutualExclusive) {
		super(label == null ? stratification.getRecordPerspective().getLabel() + ": " + group.getLabel() : label);
		this.stratification = stratification;
		this.group = group;
		put(this.group, Float.NaN); // add self
		this.mutualExclusive = mutualExclusive;
	}

	@Override
	public boolean contains(TablePerspective perspective, Group elem) {
		if (super.contains(perspective, elem))
			return true;
		if (Objects.equals(perspective, stratification))
			return true;
		if (mutualExclusive && isSameGroup(perspective, elem))
			return true;
		return false;
	}

	/**
	 * checks whether we the given group refers to the same group within different stratifications within the same
	 * categorical datadomain. e.g. mutated and mutated in two genes
	 *
	 * @param perspective
	 * @param elem
	 * @return
	 */
	private boolean isSameGroup(TablePerspective perspective, Group elem) {
		if (elem == null || !Objects.equals(elem.getGroupIndex(), group.getGroupIndex()))
			return false; // same group, simple/fast tests first
		ATableBasedDataDomain dataDomain = this.stratification.getDataDomain();
		if (!DataDomainOracle.isCategoricalDataDomain(dataDomain))
			return false;
		ATableBasedDataDomain dataDomain2 = perspective.getDataDomain();
		if (!dataDomain.equals(dataDomain2))
			return false;
		return true;
	}

	/**
	 * @return the mutualExclusive, see {@link #mutualExclusive}
	 */
	public boolean isMutualExclusive() {
		return mutualExclusive;
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