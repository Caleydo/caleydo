/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.score;

import java.util.Objects;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.tourguide.api.compute.ComputeElement;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.score.IGroupScore;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AReferenceGroupScore extends AComputedGroupScore implements IGroupScore {
	protected Perspective stratification;
	protected Group group;

	public AReferenceGroupScore(String label, Perspective stratification, Group group, Color color, Color bgColor) {
		super(label == null ? stratification.getLabel() + ": " + group.getLabel() : label, stratification
				.getDataDomain().getColor(), stratification.getDataDomain().getColor().brighter().brighter());
		this.stratification = stratification;
		this.group = group;
		put(this.group, Float.NaN); // add self
	}

	@Override
	public boolean contains(IComputeElement perspective, Group elem) {
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

	@Override
	public IComputeElement asComputeElement() {
		return new ComputeElement(stratification);
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
