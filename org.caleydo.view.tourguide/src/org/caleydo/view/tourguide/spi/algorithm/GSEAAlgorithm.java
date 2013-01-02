package org.caleydo.view.tourguide.spi.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.ARecordPerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class GSEAAlgorithm implements IStratificationAlgorithm {
	private static final int NPERM = 1000;

	private final float p; // An exponent p to control the weight of the step.
	private final Map<Integer, Float> correlation = Maps.newLinkedHashMap();
	private final List<List<Integer>> permutations = Lists.newArrayListWithExpectedSize(NPERM);

	private final TablePerspective stratification;
	private final Group group;

	public GSEAAlgorithm(TablePerspective stratification, Group group, float p) {
		this.p = p;
		this.stratification = stratification;
		this.group = group;
	}

	private void init() {
		if (!correlation.isEmpty())
			return;
		DataTable table = stratification.getDataDomain().getTable();
		List<Integer> rowIDs = stratification.getRecordPerspective().getVirtualArray()
				.getIDsOfGroup(group.getGroupIndex());
		List<Integer> colIDs = stratification.getDimensionPerspective().getVirtualArray().getIDs();

		List<Pair<Integer, Float>> g = new ArrayList<>(colIDs.size());
		for (Integer colID : colIDs) {
			// mean of the expressions level of the samples for the given gen.
			float value = 0;
			int count = 0;
			for (Integer rowID : rowIDs) {
				Float v = table.getFloat(DataRepresentation.NORMALIZED, rowID, colID);
				if (v != null && !v.isNaN() && !v.isInfinite()) {
					count++;
					value += v;
				}
			}
			g.add(Pair.make(colID, count == 0 ? 0 : value / count));
		}

		Collections.sort(g);
		for (Pair<Integer, Float> entry : g)
			correlation.put(entry.getFirst(), entry.getSecond());

		List<Integer> template = new ArrayList<>(correlation.keySet());
		for (int i = 0; i < NPERM; ++i) {
			List<Integer> per = new ArrayList<>(template);
			Collections.shuffle(per);
			permutations.add(per);
		}
	}

	@Override
	public IDType getTargetType(ARecordPerspective a, ARecordPerspective b) {
		return stratification.getDimensionPerspective().getIdType();
	}

	public float compute(Set<Integer> geneSet) {
		init();
		float es = (float)enrichmentScore(correlation.keySet(), geneSet);
		return es;
	}

	public float computePValue(Set<Integer> geneSet) {
		init();
		float es = (float)enrichmentScore(correlation.keySet(), geneSet);
		if (Float.isNaN(es))
			return Float.NaN;

		float others = 0f;
		for(List<Integer> permutation : this.permutations) {
			float phi = (float)enrichmentScore(permutation, geneSet);
			if (phi >= es)
				others += phi;
		}
		float pValue = others / NPERM;
		return pValue;
	}

	private double enrichmentScore(Iterable<Integer> ranking, Set<Integer> geneS) {
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

		for (Integer r : ranking) {
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
		return new IStratificationAlgorithm() {
			@Override
			public IDType getTargetType(ARecordPerspective a, ARecordPerspective b) {
				return GSEAAlgorithm.this.getTargetType(a, b);
			}

			@Override
			public String getAbbreviation() {
				return GSEAAlgorithm.this.getAbbreviation();
			}

			@Override
			public float compute(List<Set<Integer>> a, List<Set<Integer>> b) {
				return computePValue(a.iterator().next());
			}
		};
	}
}
