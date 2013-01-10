package org.caleydo.view.tourguide.impl.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.ARecordPerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.tourguide.spi.algorithm.IStratificationAlgorithm;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class GSEAAlgorithm implements IStratificationAlgorithm {
	private static final int NPERM = 1000;

	private final float p; // An exponent p to control the weight of the step.
	private final Map<Integer, Float> correlation = Maps.newLinkedHashMap();
	private final List<Map<Integer, Float>> permutations = Lists.newArrayListWithExpectedSize(NPERM);

	private final TablePerspective stratification;
	private final Group group;

	public GSEAAlgorithm(TablePerspective stratification, Group group, float p) {
		this.p = p;
		this.stratification = stratification;
		this.group = group;
	}

	/**
	 * @return the stratification, see {@link #stratification}
	 */
	public TablePerspective getStratification() {
		return stratification;
	}

	/**
	 * @return the group, see {@link #group}
	 */
	public Group getGroup() {
		return group;
	}

	private void init() {
		if (!correlation.isEmpty())
			return;
		final List<Integer> inA = stratification.getRecordPerspective().getVirtualArray()
				.getIDsOfGroup(group.getGroupIndex());
		this.correlation.putAll(rankedSet(inA));

		int sampleSize = inA.size();
		List<Integer> base = new ArrayList<>(stratification.getRecordPerspective().getVirtualArray().getIDs());

		// Randomly assign the original phenotype labels to samples,reorder genes, and re-compute ES(S)
		for (int i = 0; i < NPERM; ++i) {
			// shuffle randomly the ids
			Collections.shuffle(base);
			// select the first sampleSize elements as new class labels
			Collection<Integer> in = base.subList(0, sampleSize);
			permutations.add(rankedSet(in));
		}
	}

	private Map<Integer, Float> rankedSet(Collection<Integer> inA) {
		Stopwatch w = new Stopwatch().start();
		inA = new HashSet<>(inA);

		DataTable table = stratification.getDataDomain().getTable();

		List<Integer> rows = stratification.getRecordPerspective().getVirtualArray().getIDs();
		List<Integer> cols = stratification.getDimensionPerspective().getVirtualArray().getIDs();

		List<Pair<Integer, Float>> g = new ArrayList<>(cols.size());
		// List<Float> avalues = new ArrayList<>(inA.size());
		// List<Float> bvalues = new ArrayList<>(rows.size() - inA.size());
		// System.out.println(w + " " + inA.size() + " " + rows.size() + " cols " + cols.size());

		for (Integer col : cols) {
			// mean of the expressions level of the samples for the given gen.
			float asum = 0;
			int acount = 0;
			float bsum = 0;
			int bcount = 0;
			// avalues.clear();
			// bvalues.clear();
			for (Integer row : rows) {
				Float v = table.getNormalizedValue(row, col);
				if (v == null || v.isNaN() || v.isInfinite())
					continue;
				if (inA.contains(row)) {
					asum += v;
					acount++;
					// avalues.add(v);
				} else {
					bsum += v;
					bcount++;
					// bvalues.add(v);
				}

			}
			// now some kind of correlation between the two
			g.add(Pair.make(col, (asum / acount) / (bsum / bcount)));// correlationOf(avalues, bvalues)));
		}

		// System.out.println("computed " + w);
		Map<Integer, Float> correlation = Maps.newLinkedHashMap();
		Collections.sort(g, Collections.reverseOrder());
		for (Pair<Integer, Float> entry : g)
			correlation.put(entry.getFirst(), entry.getSecond());
		System.out.println(w);
		return correlation;
	}

	@Override
	public IDType getTargetType(ARecordPerspective a, ARecordPerspective b) {
		return stratification.getDimensionPerspective().getIdType();
	}

	public float compute(Set<Integer> geneSet) {
		if (geneSet.isEmpty())
			return Float.NaN;
		init();
		float es = (float) enrichmentScore(correlation, geneSet);
		return es;
	}

	public float computePValue(Set<Integer> geneSet) {
		if (geneSet.isEmpty())
			return Float.NaN;
		init();
		float es = (float) enrichmentScore(correlation, geneSet);
		if (Float.isNaN(es))
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
	public float compute(List<Set<Integer>> a, List<Set<Integer>> b) {
		return compute(a.iterator().next());
	}

	@Override
	public String getAbbreviation() {
		return "GSEA";
	}

	public IStratificationAlgorithm asPValue() {
		return new GSEAAlgorithmPValue(this);
	}

	public static class GSEAAlgorithmPValue implements IStratificationAlgorithm {
		private final GSEAAlgorithm underlying;

		private GSEAAlgorithmPValue(GSEAAlgorithm underlying) {
			this.underlying = underlying;
		}

		@Override
		public IDType getTargetType(ARecordPerspective a, ARecordPerspective b) {
			return underlying.getTargetType(a, b);
		}

		@Override
		public String getAbbreviation() {
			return underlying.getAbbreviation();
		}

		@Override
		public float compute(List<Set<Integer>> a, List<Set<Integer>> b) {
			return underlying.computePValue(a.iterator().next());
		}
	}

	public static Pair<TablePerspective, Group> resolve(IStratificationAlgorithm algorithm) {
		if (algorithm instanceof GSEAAlgorithmPValue)
			algorithm = ((GSEAAlgorithmPValue) algorithm).underlying;
		if (algorithm instanceof GSEAAlgorithm)
			return Pair.make(((GSEAAlgorithm) algorithm).getStratification(), ((GSEAAlgorithm) algorithm).getGroup());
		return null;
	}
}
