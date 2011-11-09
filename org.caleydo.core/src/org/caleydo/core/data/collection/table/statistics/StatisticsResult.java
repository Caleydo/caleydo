package org.caleydo.core.data.collection.table.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.container.FoldChangeSettings.FoldChangeEvaluator;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.conversion.ConversionTools;

@Deprecated
public class StatisticsResult {

	/**
	 * The set for which the result is valid.
	 */

	// FIXME: just for uncertainty paper. this needs to be calculated here and not inside the view
	double[] aggregatedUncertainty;

	public void clearStatisticsResults() {
	}

	@Deprecated
	public void setAggregatedUncertainty(double[] aggregatedUncertainty) {
		this.aggregatedUncertainty = aggregatedUncertainty;
	}

	public double[] getAggregatedUncertainty() {
		return aggregatedUncertainty;
	}

}
