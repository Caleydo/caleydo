/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.score;

import org.caleydo.core.util.color.Color;

import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.score.ISerializeableScore;
import org.caleydo.view.tourguide.internal.external.AExternalScoreParseSpecification;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AExternalScore extends DefaultLabelProvider implements ISerializeableScore {
	private float mappingMin;
	private float mappingMax;
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
		boolean tnan = Float.isNaN(mappingMax);
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
