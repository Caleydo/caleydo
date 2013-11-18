/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.impl;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IIDTypeMapper;
import org.eclipse.core.runtime.IProgressMonitor;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;

public class GSEAAlgorithm extends AGSEAAlgorithm {
	private static final int NPERM = 1000;

	private final int p; // An exponent p to control the weight of the step.
	private Map<Integer, Float> correlation;
	private List<Map<Integer, Float>> permutations;
	private final boolean withPValue;


	public GSEAAlgorithm(Perspective perspective, Group group, int p, boolean withPValue) {
		super(perspective, group);
		this.p = p;
		this.withPValue = withPValue;

	}


	@Override
	public void init(IProgressMonitor monitor) {
		if (correlation != null)
			return;
		final List<Integer> inA = perspective.getVirtualArray()
				.getIDsOfGroup(group.getGroupIndex());
		if (monitor.isCanceled())
			return;

		// convert to primary = DAVID
		this.correlation = rankedSet(new RankedSet(inA));
		if (monitor.isCanceled()) {
			correlation = null;
			return;
		}

		if (!withPValue)
			return;

		int sampleSize = inA.size();
		List<Integer> base = new ArrayList<>(perspective.getVirtualArray().getIDs());

		// Randomly assign the original phenotype labels to samples,reorder genes, and re-compute ES(S)
		List<RankedSet> sets = new ArrayList<>(NPERM);
		monitor.setTaskName("Computing " + NPERM + " random permutations");
		for (int i = 0; i < NPERM; ++i) {
			// shuffle randomly the ids
			Collections.shuffle(base);
			// select the first sampleSize elements as new class labels
			Collection<Integer> in = base.subList(0, sampleSize);
			sets.add(new RankedSet(in));
			if (i % 50 == 0)
				monitor.setTaskName("Computing " + NPERM + " random permutations (" + i + ")");
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
		List<Integer> cols = table.getDefaultDimensionPerspective(false).getVirtualArray().getIDs();

		// # Compute observed and random permutation gene rankings
		//
		// obs.s2n <- vector(length=N, mode="numeric")
		// signal.strength <- vector(length=Ng, mode="numeric")
		// tag.frac <- vector(length=Ng, mode="numeric")
		// gene.frac <- vector(length=Ng, mode="numeric")
		// coherence.ratio <- vector(length=Ng, mode="numeric")
		// obs.phi.norm <- matrix(nrow = Ng, ncol = nperm)
		// correl.matrix <- matrix(nrow = N, ncol = nperm)
		// obs.correl.matrix <- matrix(nrow = N, ncol = nperm)
		// order.matrix <- matrix(nrow = N, ncol = nperm)
		// obs.order.matrix <- matrix(nrow = N, ncol = nperm)
		//
		// nperm.per.call <- 100
		// n.groups <- nperm %/% nperm.per.call
		// n.rem <- nperm %% nperm.per.call
		// n.perms <- c(rep(nperm.per.call, n.groups), n.rem)
		// n.ends <- cumsum(n.perms)
		// n.starts <- n.ends - n.perms + 1
		//
		// if (n.rem == 0) {
		// n.tot <- n.groups
		// } else {
		// n.tot <- n.groups + 1
		// }
		//
		// for (nk in 1:n.tot) {
		// call.nperm <- n.perms[nk]
		//
		// print(paste("Computing ranked list for actual and permuted phenotypes.......permutations: ", n.starts[nk],
		// "--", n.ends[nk], sep=" "))
		//
		// O <- GSEA.GeneRanking(A, class.labels, gene.labels, call.nperm, permutation.type = perm.type,
		// sigma.correction = "GeneCluster", fraction=fraction, replace=replace, reverse.sign = reverse.sign)
		// gc()
		//
		// order.matrix[,n.starts[nk]:n.ends[nk]] <- O$order.matrix
		// obs.order.matrix[,n.starts[nk]:n.ends[nk]] <- O$obs.order.matrix
		// correl.matrix[,n.starts[nk]:n.ends[nk]] <- O$s2n.matrix
		// obs.correl.matrix[,n.starts[nk]:n.ends[nk]] <- O$obs.s2n.matrix
		// rm(O)
		// }
		//
		// obs.s2n <- apply(obs.correl.matrix, 1, median) # using median to assign enrichment scores
		// obs.index <- order(obs.s2n, decreasing=T)
		// obs.s2n <- sort(obs.s2n, decreasing=T)

		for (Integer col : cols) {

			// System.out.println(table.getDataDomain().getDimensionLabel(col));
			for (Integer row : rows) {
				Float v = table.getRaw(col, row);
				if (v == null || v.isNaN() || v.isInfinite())
					continue;
				inA.add(row, v);
			}
			inA.flush(col);
			// System.out.println(table.getDataDomain().getDimensionLabel(col) + " " + inA.correlation.get(col));
		}
		System.out.println(w);
		return inA.toSignal2Noise(dim2primary);
	}

	private List<Map<Integer, Float>> rankedSet(List<RankedSet> sets, IProgressMonitor monitor) {
		Stopwatch w = new Stopwatch().start();

		ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) perspective.getDataDomain();
		Table table = dataDomain.getTable();

		List<Integer> rows = perspective.getVirtualArray().getIDs();
		List<Integer> cols = table.getDefaultDimensionPerspective(false).getVirtualArray().getIDs();
		for (Integer col : cols) {
			for (Integer row : rows) {
				Float v = table.getRaw(col, row);
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
			rs.add(s.toSignal2Noise(dim2primary));
		System.out.println(w);
		return rs;
	}

	private static class RankedSet {
		// mean of the expressions level of the samples for the given gen.
		private double aTimesP = 0;
		private double aSquaredTimesP = 0;
		private int n1 = 0;
		private double bTimesP = 0;
		private double bsquaredsum = 0;
		private int n2 = 0;

		private final BitSet inA;
		private final Map<Integer, Float> correlation = Maps.newTreeMap();

		public RankedSet(Collection<Integer> inA) {
			this.inA = new BitSet(inA.size());
			for (Integer v : inA)
				this.inA.set(v);
		}

		public void add(Integer row, double v) {
			if (inA.get(row)) {
				aTimesP += v;
				double t = v * v;
				aSquaredTimesP += t;
				n1++;
				// avalues.add(v);
			} else {
				bTimesP += v;
				bsquaredsum += v * v;
				n2++;
				// bvalues.add(v);
			}
		}

		public Map<Integer, Float> toSignal2Noise(IIDTypeMapper<Integer, Integer> dim2primary) {
			LinkedHashMap<Integer, Float> r = new LinkedHashMap<>();
			List<Entry<Integer, Float>> entrySet = new ArrayList<>(correlation.entrySet());
			// sort correlation in decreasing order;
			Collections.sort(entrySet, new Comparator<Entry<Integer, Float>>() {
				@Override
				public int compare(Entry<Integer, Float> o1, Entry<Integer, Float> o2) {
					return -Float.compare(o1.getValue(), o2.getValue());
				}
			});
			for (Entry<Integer, Float> ri : entrySet) {
				Set<Integer> keys = dim2primary.apply(ri.getKey());
				if (keys == null) {
					// dim2primary.apply(ri.getKey());
					continue;
				}
				for (Integer key : keys)
					r.put(key, ri.getValue());
			}
			return r;
		}

		public void flush(int col) {
			// M1 <- A %*% P
			// M1 <- M1/n1
			// A2 <- A*A
			// S1 <- A2 %*% P
			// S1 <- S1/n1 - M1*M1
			// S1 <- sqrt(abs((n1/(n1-1)) * S1))
			double m1 = aTimesP / n1;
			double s1 = aSquaredTimesP / n1 - m1 * m1;
			s1 = sqrt(abs((n1 / (n1 - 1)) * s1));

			double m2 = bTimesP / n2;
			double s2 = bsquaredsum / n2 - m2 * m2;
			s2 = sqrt(abs((n2 / (n2 - 1)) * s2));

			// if (sigma.correction == "GeneCluster") { # small sigma "fix" as used in GeneCluster
			// S2 <- ifelse(0.2*abs(M2) < S2, S2, 0.2*abs(M2))
			// S2 <- ifelse(S2 == 0, 0.2, S2)
			// S1 <- ifelse(0.2*abs(M1) < S1, S1, 0.2*abs(M1))
			// S1 <- ifelse(S1 == 0, 0.2, S1)
			// gc()
			// }
			s1 = (0.2 * abs(m1) < s1) ? s1 : 0.2 * abs(m1);
			s1 = (s1 == 0.f) ? 0.2 : s1;
			s2 = (0.2 * abs(m2) < s2) ? s2 : 0.2 * abs(m2);
			s2 = (s2 == 0.f) ? 0.2 : s2;

			double s = s1 + s2;
			double m = m1 - m2;
			float s2n = (float) (m / s);

			correlation.put(col, s2n);// correlationOf(avalues, bvalues)));
			aTimesP = aSquaredTimesP = n1 = 0;
			bTimesP = bsquaredsum = n2 = 0;
		}
	}

	@Override
	protected float computeImpl(Set<Integer> geneSet, IProgressMonitor monitor) {
		float es = (float) enrichmentScore(correlation, geneSet);
		return es;
	}

	@Override
	protected float computePValueImpl(Set<Integer> geneSet, IProgressMonitor monitor) {
		if (!withPValue)
			return Float.NaN;
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
		// #
		// # Computes the weighted GSEA score of gene.set in gene.list.
		// # The weighted score type is the exponent of the correlation
		// # weight: 0 (unweighted = Kolmogorov-Smirnov), 1 (weighted), and 2 (over-weighted). When the score type is 1
		// or 2 it is
		// # necessary to input the correlation vector with the values in the same order as in the gene list.
		// #
		// # Inputs:
		// # gene.list: The ordered gene list (e.g. integers indicating the original position in the input dataset)
		// # gene.set: A gene set (e.g. integers indicating the location of those genes in the input dataset)
		// # weighted.score.type: Type of score: weight: 0 (unweighted = Kolmogorov-Smirnov), 1 (weighted), and 2
		// (over-weighted)
		// # correl.vector: A vector with the coorelations (e.g. signal to noise scores) corresponding to the genes in
		// the gene list
		// #
		// # Outputs:
		// # ES: Enrichment score (real number between -1 and +1)
		//
		// tag.indicator <- sign(match(gene.list, gene.set, nomatch=0)) # notice that the sign is 0 (no tag) or 1 (tag)
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
		// return(list(ES = ES, arg.ES = arg.ES, RES = RES, indicator = tag.indicator))
		// }
		final int N = correlation.size();
		final int N_h = geneS.size(); // real size not the only in this subset stratification

		if (N_h == 0)
			return Float.NaN;

		float N_R = 0;
		int intersection = 0;
		for(Integer g: geneS)
			if (correlation.containsKey(g)) {
				N_R += Math.abs(powP(correlation.get(g)));
				intersection++;
			}

		if (intersection == 0)
			return Float.NaN;

		final double norm_tag = 1 / N_R;
		final double norm_no_tag = 1. / (N - N_h); // watch out all integers!

		double p_hit = 0;
		double p_miss = 0;

		double es_max = Double.NEGATIVE_INFINITY;
		double es_min = Double.POSITIVE_INFINITY;

		for (Integer r : correlation.keySet()) {
			if (geneS.contains(r))
				p_hit += Math.abs(powP(correlation.get(r))) * norm_tag;
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

	/**
	 * @param float1
	 * @param p2
	 * @return
	 */
	private double powP(float v) {
		switch (p) {
		case 0:
			return 1;
		case 1:
			return v;
		case 2:
			return v * v;
		case 3:
			return v * v * v;
		default:
			return Math.pow(v, p);
		}
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
