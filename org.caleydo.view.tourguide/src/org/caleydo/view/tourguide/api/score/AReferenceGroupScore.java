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

import java.awt.Color;
import java.util.Objects;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.color.Colors;
import org.caleydo.view.tourguide.spi.score.IGroupScore;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AReferenceGroupScore extends AComputedGroupScore implements IGroupScore {
	protected Perspective stratification;
	protected Group group;

	public AReferenceGroupScore(String label, Perspective stratification, Group group, Color color, Color bgColor) {
		super(label == null ? stratification.getLabel() + ": " + group.getLabel() : label, Colors.of(stratification
				.getDataDomain().getColor()), Colors.of(stratification.getDataDomain().getColor()).brighter()
				.brighter());
		this.stratification = stratification;
		this.group = group;
		put(this.group, Float.NaN); // add self
	}

	@Override
	public boolean contains(Perspective perspective, Group elem) {
		if (super.contains(perspective, elem))
			return true;
		if (Objects.equals(perspective, stratification))
			return true;
		return false;
	}

	@Override
	public String getProviderName() {
		return stratification.getLabel();
	}

	/**
	 * @return the stratification
	 */
	@Override
	public Perspective getStratification() {
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + ((stratification == null) ? 0 : stratification.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AReferenceGroupScore other = (AReferenceGroupScore) obj;
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