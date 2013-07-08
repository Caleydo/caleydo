/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
