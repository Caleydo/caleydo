package org.caleydo.view.tourguide.impl.algorithm;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.eclipse.core.runtime.IProgressMonitor;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;

public class GSEAAlgorithm extends AGSEAAlgorithm {
	private static final int NPERM = 1000;

	private final float p; // An exponent p to control the weight of the step.
	private Map<Integer, Float> correlation;
	private List<Map<Integer, Float>> permutations;


	public GSEAAlgorithm(Perspective perspective, Group group, float p) {
		super(perspective, group);
		this.p = p;

	}


	@Override
	public void init(IProgressMonitor monitor) {
		if (correlation != null)
			return;
		final List<Integer> inA = perspective.getVirtualArray()
				.getIDsOfGroup(group.getGroupIndex());
		if (monitor.isCanceled())
			return;
		this.correlation = rankedSet(new RankedSet(inA));
		if (monitor.isCanceled()) {
			correlation = null;
			return;
		}

		int sampleSize = inA.size();
		List<Integer> base = new ArrayList<>(perspective.getVirtualArray().getIDs());

		// Randomly assign the original phenotype labels to samples,reorder genes, and re-compute ES(S)
		List<RankedSet> sets = new ArrayList<>(NPERM);
		for (int i = 0; i < NPERM; ++i) {
			// shuffle randomly the ids
			Collections.shuffle(base);
			// select the first sampleSize elements as new class labels
			Collection<Integer> in = base.subList(0, sampleSize);
			sets.add(new RankedSet(in));
		}

		if (monitor.isCanceled()) {
			// undo initialization
			correlation = null;
			return;
		}

		permutations = rankedSet(sets, monitor);
		if (monitor.isCanceled()) {
			correlation = null;
			permutations = null;
		}
	}

	private Map<Integer, Float> rankedSet(RankedSet inA) {
		Stopwatch w = new Stopwatch().start();

		ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) perspective.getDataDomain();
		Table table = dataDomain.getTable();

		List<Integer> rows = perspective.getVirtualArray().getIDs();
		List<Integer> cols = table.getDefaultDimensionPerspective().getVirtualArray().getIDs();

		for (Integer col : cols) {

			for (Integer row : rows) {
				Float v = table.getNormalizedValue(col, row);
				if (v == null || v.isNaN() || v.isInfinite())
					continue;
				inA.add(row, v);
			}
			inA.flush(col);
		}
		System.out.println(w);
		return inA.correlation;
	}

	private List<Map<Integer, Float>> rankedSet(List<RankedSet> sets, IProgressMonitor monitor) {
		Stopwatch w = new Stopwatch().start();

		ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) perspective.getDataDomain();
		Table table = dataDomain.getTable();

		List<Integer> rows = perspective.getVirtualArray().getIDs();
		List<Integer> cols = table.getDefaultDimensionPerspective().getVirtualArray().getIDs();

		for (Integer col : cols) {
			for (Integer row : rows) {
				Float v = table.getNormalizedValue(col, row);
				if (v == null || v.isNaN() || v.isInfinite())
					continue;
				for (RankedSet s : sets)
					s.add(row, v);
			}
			for (RankedSet s : sets)
				s.flush(col);
			if (monitor.isCanceled())
				return null;
		}
		List<Map<Integer, Float>> rs = new ArrayList<>(sets.size());
		for (RankedSet s : sets)
			rs.add(s.correlation);
		System.out.println(w);
		return rs;
	}

	private static class RankedSet {
		// mean of the expressions level of the samples for the given gen.
		private float asum = 0;
		private int acount = 0;
		private float bsum = 0;
		private int bcount = 0;

		private final BitSet inA;
		private final Map<Integer, Float> correlation = Maps.newTreeMap();

		public RankedSet(Collection<Integer> inA) {
			this.inA = new BitSet(inA.size());
			for (Integer v : inA)
				this.inA.set(v);
		}
		public void add(Integer row, float v) {
			if (inA.get(row)) {
				asum += v;
				acount++;
				// avalues.add(v);
			} else {
				bsum += v;
				bcount++;
				// bvalues.add(v);
			}
		}

		public void flush(int col) {
			// now some kind of correlation between the two
			correlation.put(col, (asum / acount) / (bsum / bcount));// correlationOf(avalues, bvalues)));
			asum = acount = 0;
			bsum = bcount = 0;
		}
	}

	@Override
	protected float computeImpl(Set<Integer> geneSet, IProgressMonitor monitor) {
		float es = (float) enrichmentScore(correlation, geneSet);
		return es;
	}

	@Override
	protected float computePValueImpl(Set<Integer> geneSet, IProgressMonitor monitor) {
		float es = (float) enrichmentScore(correlation, geneSet);
		if (Float.isNaN(es))
			return Float.NaN;

		if (monitor.isCanceled())
			return Float.NaN;

		// Randomly assign the original phenotype labels to samples,reorder genes, and re-compute ES(S)
		// Repeat step 1 for 1,000 permutations, and create a histogram of
		// the corresponding enrichment scores ES NULL .
		// 3. Estimate nominal P value for S from ES NULL by using the
		// positive or negative portion of the distribution corresponding to
		// the sign of the observed ES(S).
		//

		// pos.phi <- NULL
		// neg.phi <- NULL
		// for (j in 1:nperm) {
		// if (phi[i, j] >= 0) {
		// pos.phi <- c(pos.phi, phi[i, j])
		// } else {
		// neg.phi <- c(neg.phi, phi[i, j])
		// }
		// }
		// ES.value <- Obs.ES[i]
		// if (ES.value >= 0) {
		// p.vals[i, 1] <- signif(sum(pos.phi >= ES.value)/length(pos.phi), digits=5)
		// } else {
		// p.vals[i, 1] <- signif(sum(neg.phi <= ES.value)/length(neg.phi), digits=5)
		// }
		//
		int phiCount = 0;
		float phiSum = 0;
		for (Map<Integer, Float> permutation : this.permutations) {
			float phi = (float)enrichmentScore(permutation, geneSet);
			if (es >= 0) {
				if (phi >= 0)
					phiCount++;
				if (phi >= es)
					phiSum += phi;
			} else {
				if (phi < 0)
					phiCount++;
				if (phi <= es)
					phiSum += phi;
			}

			if (monitor.isCanceled())
				return Float.NaN;
		}
		float pValue = phiSum / phiCount;

		return pValue;
	}

	private double enrichmentScore(Map<Integer, Float> correlation, Set<Integer> geneS) {
		// tag.indicator <- sign(match(gene.list, gene.set, nomatch=0)) # notice that the sign is 0 (no tag) or 1
		// (tag)
		// no.tag.indicator <- 1 - tag.indicator
		// N <- length(gene.list)
		// Nh <- length(gene.set)
		// Nm <- N - Nh
		// if (weighted.score.type == 0) {
		// correl.vector <- rep(1, N)
		// }
		// alpha <- weighted.score.type
		// correl.vector <- abs(correl.vector**alpha)
		// sum.correl.tag <- sum(correl.vector[tag.indicator == 1])
		// norm.tag <- 1.0/sum.correl.tag
		// norm.no.tag <- 1.0/Nm
		// RES <- cumsum(tag.indicator * correl.vector * norm.tag - no.tag.indicator * norm.no.tag)
//		   max.ES <- max(RES)
//		   min.ES <- min(RES)
//		   if (max.ES > - min.ES) {
		// # ES <- max.ES
		// ES <- signif(max.ES, digits = 5)
//		      arg.ES <- which.max(RES)
//		   } else {
		// # ES <- min.ES
//		      ES <- signif(min.ES, digits=5)
//		      arg.ES <- which.min(RES)
//		   }
		final int N = correlation.size();
		final int N_h = geneS.size();

		if (N_h == 0)
			return Float.NaN;

		float N_R = 0;
		for(Integer g: geneS)
			if (correlation.containsKey(g))
				N_R += Math.abs(Math.pow(correlation.get(g), p));

		final double norm_tag = 1 / N_R;
		final double norm_no_tag = 1 / (N - N_h);

		double p_hit = 0;
		double p_miss = 0;

		double es_max = Double.MIN_VALUE;
		double es_min = Double.MAX_VALUE;

		for (Integer r : correlation.keySet()) {
			if (geneS.contains(r))
				p_hit += Math.abs(Math.pow(correlation.get(r), p)) / norm_tag;
			else
				p_miss += norm_no_tag;
			double esi = p_hit - p_miss;
			if (esi > es_max)
				es_max = esi;
			if (esi < es_min)
				es_min = esi;
		}

		if (es_max > -es_min)
			return es_max;
		else
			return es_min;
	}

	@Override
	public String getAbbreviation() {
		return "GSEA";
	}

	@Override
	public String getDescription() {
		return "GSEA score against ";
	}
}
