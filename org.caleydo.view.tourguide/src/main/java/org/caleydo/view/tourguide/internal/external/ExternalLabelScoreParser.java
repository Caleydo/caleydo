/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.external;

import java.util.Map;

import org.caleydo.view.tourguide.internal.score.ExternalLabelScore;

public class ExternalLabelScoreParser extends
		AExternalScoreParser<ExternalLabelParseSpecification, String> {

	public ExternalLabelScoreParser(ExternalLabelParseSpecification spec) {
		super(spec);
	}

	@Override
	protected String extractID(String originalID) {
		return originalID;
	}

	@Override
	protected ExternalLabelScore createScore(String label, boolean isRank, Map<String, Double> scores) {
		return new ExternalLabelScore(label, spec, scores);
	}

}
