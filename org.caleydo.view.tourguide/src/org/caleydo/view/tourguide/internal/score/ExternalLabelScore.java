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
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.score.ISerializeableScore;
import org.caleydo.view.tourguide.internal.external.ExternalLabelParseSpecification;
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
public final class ExternalLabelScore extends AExternalScore implements ISerializeableScore {
	private Map<String, Float> scores = new HashMap<>();
	private String dataDomainID;

	public ExternalLabelScore() {
		super();
	}

	public ExternalLabelScore(String label, ExternalLabelParseSpecification spec,
			Map<String, Float> scores) {
		super(label, spec);
		this.dataDomainID = spec.getDataDomainID();
		this.scores.putAll(scores);
	}

	@Override
	public final float apply(IComputeElement elem, Group g) {
		if (!elem.getDataDomain().getDataDomainID().equals(dataDomainID))
			return Float.NaN;
		String label = elem.getLabel();
		Float v = this.scores.get(label);
		if (v == null)
			return Float.NaN;
		return v.floatValue();
	}

	@Override
	public boolean supports(EDataDomainQueryMode mode) {
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(dataDomainID, getLabel());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExternalLabelScore other = (ExternalLabelScore) obj;
		if (Objects.equal(dataDomainID, other.dataDomainID))
			return false;
		if (Objects.equal(getLabel(), other.getLabel()))
			return false;
		return true;
	}
}
