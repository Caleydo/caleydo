/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.score;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.vis.lineup.model.mapping.JavaScriptFunctions;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public enum ECombinedOperator implements Function<float[], Float>, ILabeled {
	MAX, MIN, MEAN, MEDIAN, GEOMETRIC_MEAN;

	@Override
	public Float apply(float[] data) {
		return combine(data);
	}

	public String getAbbreviation() {
		switch (this) {
		case GEOMETRIC_MEAN:
			return "GEO";
		case MAX:
			return "MAX";
		case MEAN:
			return "AVG";
		case MEDIAN:
			return "MED";
		case MIN:
			return "MIN";
		}
		throw new IllegalStateException("unknown operator: " + this);
	}

	@Override
	public String getLabel() {
		switch (this) {
		case GEOMETRIC_MEAN:
			return "Geometric Mean";
		case MAX:
			return "Maximum";
		case MEAN:
			return "Average";
		case MEDIAN:
			return "Median";
		case MIN:
			return "Minium";
		}
		throw new IllegalStateException("unknown operator: " + this);
	}

	public float combine(float[] data) {
		switch (this) {
		case MAX:
			return JavaScriptFunctions.max(data);
		case MIN:
			return JavaScriptFunctions.min(data);
		case MEAN:
			return JavaScriptFunctions.mean(data);
		case GEOMETRIC_MEAN:
			return JavaScriptFunctions.geometricMean(data);
		case MEDIAN:
			return JavaScriptFunctions.median(data);
		}
		throw new IllegalStateException("unknown operator: " + this);
	}
}
