package org.caleydo.view.tourguide.v2.r.model;

import java.util.HashMap;
import java.util.Map;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;

class ScoreDataDomain extends ATableBasedDataDomain {
	private Map<Integer, ScoreColumn> metaData = new HashMap<>();

	// combined selection
	private Selection combinedSelection = new Selection();

	public ScoreColumn getMetaData(Integer col) {
		return metaData.get(col);
	}
}
