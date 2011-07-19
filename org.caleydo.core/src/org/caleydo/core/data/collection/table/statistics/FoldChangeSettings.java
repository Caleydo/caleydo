package org.caleydo.core.data.collection.table.statistics;

public class FoldChangeSettings {

	public enum FoldChangeEvaluator {
		GREATER,
		LESS,
		BOTH
	}

	double ratio = -1;
	
	double ratioUncertainty = -1;

	FoldChangeEvaluator evaluator;

	public FoldChangeSettings(double ratio, double ratioUncertainty, FoldChangeEvaluator evaluator) {
		this.ratio = ratio;
		this.ratioUncertainty = ratioUncertainty;
		this.evaluator = evaluator;
	}

	public FoldChangeEvaluator getEvaluator() {
		return evaluator;
	}

	public double getRatio() {
		return ratio;
	}
	
	public double getRatioUncertainty() {
		return ratioUncertainty;
	}
}
