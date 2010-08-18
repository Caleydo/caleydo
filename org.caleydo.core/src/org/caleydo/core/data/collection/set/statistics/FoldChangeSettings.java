package org.caleydo.core.data.collection.set.statistics;

public class FoldChangeSettings {

	public enum FoldChangeEvaluator {
		GREATER,
		LESS,
		SAME
	}

	double ratio = -1;

	FoldChangeEvaluator evaluator;

	public FoldChangeSettings(double ratio, FoldChangeEvaluator evaluator) {
		this.ratio = ratio;
		this.evaluator = evaluator;
	}

	public FoldChangeEvaluator getEvaluator() {
		return evaluator;
	}

	public double getRatio() {
		return ratio;
	}
}
