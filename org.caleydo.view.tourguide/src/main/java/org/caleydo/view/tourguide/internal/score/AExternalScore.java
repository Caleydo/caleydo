/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.score;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.tourguide.api.external.AExternalScoreParseSpecification;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.score.ISerializeableScore;
import org.caleydo.vis.lineup.model.mapping.EStandardMappings;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;

/**
 * @author Samuel Gratzl
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AExternalScore extends DefaultLabelProvider implements ISerializeableScore {
	private double mappingMin;
	private double mappingMax;
	private EStandardMappings mapping;

	private Color bgColor;
	private Color color;


	public AExternalScore() {
		super("");
	}

	public AExternalScore(String label, AExternalScoreParseSpecification spec) {
		super(label);
		this.color = spec.getColor();
		this.bgColor = spec.getColor().brighter().brighter();
		this.mappingMin = spec.getMappingMin();
		this.mappingMax = spec.getMappingMax();
		this.mapping = spec.getMapping();
	}

	@Override
	public void onRegistered() {

	}

	@Override
	public boolean supports(EDataDomainQueryMode mode) {
		return mode == EDataDomainQueryMode.STRATIFICATIONS;
	}

	@Override
	public String getAbbreviation() {
		return "EX";
	}

	@Override
	public String getDescription() {
		return "External Score " + getLabel();
	}

	@Override
	public String getProviderName() {
		return "External";
	}

	@Override
	public PiecewiseMapping createMapping() {
		boolean tnan = Double.isNaN(mappingMax);
		PiecewiseMapping m;
		if (tnan) {
			m = new PiecewiseMapping(mappingMin, mappingMax);
		} else if (mappingMin > mappingMax) {
			m = new PiecewiseMapping(mappingMax, mappingMin);
			m.put(mappingMax, 1);
			m.put(mappingMin, 0);
		} else {
			m = new PiecewiseMapping(mappingMin, mappingMax);
		}
		if (mapping != null && mapping != EStandardMappings.LINEAR)
			mapping.apply(m);
		return m;
	}

	@Override
	public Color getBGColor() {
		return bgColor;
	}

	@Override
	public Color getColor() {
		return color;
	}

}
