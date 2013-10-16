/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.external;

import java.util.Map;

import org.caleydo.view.tourguide.internal.score.ExternalGroupLabelScore;

public class ExternalGroupLabelScoreParser extends AExternalScoreParser<GroupLabelParseSpecification, String> {

	public ExternalGroupLabelScoreParser(GroupLabelParseSpecification spec) {
		super(spec);
	}

	@Override
	protected String extractID(String originalID) {
		return originalID;
	}

	@Override
	protected ExternalGroupLabelScore createScore(String label, boolean isRank, Map<String, Double> scores) {
		return new ExternalGroupLabelScore(label, spec, scores);
	}

}
