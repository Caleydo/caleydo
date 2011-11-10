package org.caleydo.core.data.collection.table.statistics;

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
