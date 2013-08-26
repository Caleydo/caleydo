/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.score;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.api.score.ISerializeableScore;
import org.caleydo.view.tourguide.internal.external.GroupLabelParseSpecification;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;

import com.google.common.base.Objects;

/**
 * external score which contains a score per group of a stratification, identified by its label
 *
 * @author Samuel Gratzl
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class ExternalGroupLabelScore extends AExternalScore implements ISerializeableScore {
	private String perspectiveKey;
	private Map<String, Double> scores = new HashMap<>();

	public ExternalGroupLabelScore() {
		super();
	}

	public ExternalGroupLabelScore(String label, GroupLabelParseSpecification spec, Map<String, Double> scores) {
		super(label, spec);
		this.perspectiveKey = spec.getPerspectiveKey();
		this.scores.putAll(scores);
	}

	@Override
	public final double apply(IComputeElement elem, Group g) {
		if (g == null)
			return Double.NaN;
		if (!perspectiveKey.equals(elem.getPersistentID()))
			return Double.NaN;
		Double v = this.scores.get(g.getLabel());
		if (v == null)
			return Double.NaN;
		return v.doubleValue();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getLabel(), perspectiveKey);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExternalGroupLabelScore other = (ExternalGroupLabelScore) obj;
		if (Objects.equal(perspectiveKey, other.perspectiveKey))
			return false;
		if (Objects.equal(getLabel(), other.getLabel()))
			return false;
		return true;
	}
}
