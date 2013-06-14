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

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.tourguide.api.compute.ComputeElement;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.score.IStratificationScore;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AReferenceStratificationScore extends AComputedStratificationScore implements
		IStratificationScore {
	protected final Perspective reference;

	public AReferenceStratificationScore(String label, Perspective reference, Color color, Color bgColor) {
		super(label == null ? reference.getLabel() : label, reference.getDataDomain().getColor(), reference
				.getDataDomain().getColor().brighter());
		this.reference = reference;
	}

	@Override
	public boolean contains(IComputeElement elem) {
		return super.contains(elem) || elem.getPersistentID().equals(getStratification().getPerspectiveID());
	}

	@Override
	public final Perspective getStratification() {
		return reference;
	}

	@Override
	public IComputeElement asComputeElement() {
		return new ComputeElement(reference);
	}

	@Override
	public String getProviderName() {
		return reference.getProviderName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((reference == null) ? 0 : reference.hashCode());
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
		AReferenceStratificationScore other = (AReferenceStratificationScore) obj;
		if (reference == null) {
			if (other.reference != null)
				return false;
		} else if (!reference.equals(other.reference))
			return false;
		return true;
	}
}