/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.util.impute;

import static java.lang.Double.isInfinite;
import static java.lang.Double.isNaN;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

import org.apache.commons.lang.StringUtils;
import org.caleydo.core.io.KNNImputeDescription;
import org.caleydo.core.util.collection.Pair;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.ImmutableTable.Builder;
import com.google.common.collect.Table;
import com.google.common.io.CharStreams;

/**
 * @author Samuel Gratzl
 *
 */
public class KNNImpute extends RecursiveTask<Table<Integer, Integer, Float>> {
	private static final long serialVersionUID = 1605596542323204167L;
	private final KNNImputeDescription desc;
	private final Random r;

	private final int samples;
	private final ImmutableList<Gene> genes;
	private final LoadingCache<Integer, Sample> samples_ = CacheBuilder.newBuilder().build(
			new CacheLoader<Integer, Sample>() {
				@Override
				public Sample load(Integer key) {
					return computeSample(key.intValue());
				}
			});

	public KNNImpute(KNNImputeDescription desc, ImmutableList<Gene> genes) {
		this.desc = desc;
		this.genes = genes;
		this.samples = this.genes.get(0).size();
		this.r = new Random(desc.getRng_seed());
	}


	@Override
	protected Table<Integer, Integer, Float> compute() {
//		data
//		An expression matrix with genes in the rows, samples in the columns
//		k
//		Number of neighbors to be used in the imputation (default=10)
//		rowmax
//		The maximum percent missing data allowed in any row (default 50%). For any
//		rows with more than rowmax% missing are imputed using the overall mean per
//		sample.
//		colmax
//		The maximum percent missing data allowed in any column (default 80%). If
//		any column has more than colmax% missing data, the program halts and reports
//		an error.
//		maxp
//		The largest block of genes imputed using the knn algorithm inside impute.knn
//		(default 1500); larger blocks are divided by two-means clustering (recursively)
//		prior to imputation. If maxp=p, only knn imputation is done.
//		rng.seed
//		The seed used for the random number generator (default 362436069) for repro-
//		ducibility.
//		impute.knn uses k-nearest neighbors in the space of genes to impute missing expression values.
//		For each gene with missing values, we find the k nearest neighbors using a Euclidean metric, con-
//		fined to the columns for which that gene is NOT missing. Each candidate neighbor might be missing
//		some of the coordinates used to calculate the distance. In this case we average the distance from
//		the non-missing coordinates. Having found the k nearest neighbors for a gene, we impute the miss-
//		ing elements by averaging those (non-missing) elements of its neighbors. This can fail if ALL the
//		neighbors are missing in a particular element. In this case we use the overall column mean for that
//		block of genes.
//		Since nearest neighbor imputation costs O(plog(p)) operations per gene, where p is the number
//		of rows, the computational time can be excessive for large p and a large number of missing rows.
//		Our strategy is to break blocks with more than maxp genes into two smaller blocks using two-mean
//		clustering. This is done recursively till all blocks have less than maxp genes. For each block, k-
//		nearest neighbor imputation is done separately. We have set the default value of maxp to 1500.
//		Depending on the speed of the machine, and number of samples, this number might be increased.
//		Making it too small is counter-productive, because the number of two-mean clustering algorithms
//		will increase.

		if (toomanyNaNsInAColumn())
			throw new IllegalStateException();

		final float rowMax = desc.getRowmax();
		final boolean validRowMax = !Float.isInfinite(rowMax) && !Float.isNaN(rowMax);
		final int max = validRowMax ? Math.round(desc.getRowmax() * samples) : 0;


		//list of possible
		List<Gene> neighborhood;
		int withMissing = 0;
		Collection<ForkJoinTask<Void>> tasks = new ArrayList<>();
		if (!validRowMax) {
			neighborhood = genes;// all genes
		} else {
			neighborhood = new ArrayList<>(genes.size());
			for (Gene gene : genes) {
				if (gene.getNaNs() == 0) {// nothing to impute
					neighborhood.add(gene);
				} else if (validRowMax && gene.getNaNs() > max) { // too many nans use the sample mean
					tasks.add(new ImputeSampleMean(gene));
					//not a good neighbor
				} else {
					// neighbor but something needs to be done
					neighborhood.add(gene);
					withMissing++;
				}
			}
		}

		if (withMissing > 0)
			tasks.add(new ImputeKNNMean(neighborhood));
		invokeAll(tasks);

		ImmutableTable.Builder<Integer, Integer, Float> b = ImmutableTable.builder();
		for (Gene gene : genes) {
			if (gene.isAnySet()) {
				gene.fillImpute(b);
			}
		}
		return b.build();
	}

	/**
	 * input by KNN mean and split in two cluster if neighborhood is too large
	 *
	 * @param neighborhood
	 */
	private class ImputeKNNMean extends RecursiveAction {
		private static final long serialVersionUID = 1L;
		private final List<Gene> neighborhood;

		public ImputeKNNMean(List<Gene> neighborhood) {
			this.neighborhood = neighborhood;
		}

		@Override
		protected void compute() {
			if (neighborhood.size() <= desc.getMaxp()) {
				Collection<ImputeKNNMeanImpl> tasks = new ArrayList<>();
				for (Gene gene : neighborhood) {
					if (gene.getNaNs() > 0)
						tasks.add(new ImputeKNNMeanImpl(neighborhood, gene));
				}
				invokeAll(tasks);
			} else {
				Pair<List<Gene>, List<Gene>> r = twoMeanClusterSplit(neighborhood);
				invokeAll(new ImputeKNNMean(r.getFirst()), new ImputeKNNMean(r.getSecond()));
			}
		}
	}

	/**
	 * split the neighbor hood in two groups based on 2 k-means
	 *
	 * @param neighborhood
	 * @return
	 */
	private Pair<List<Gene>, List<Gene>> twoMeanClusterSplit(List<Gene> neighborhood) {
		final int n = neighborhood.size();

		final int maxit = desc.getMaxit();
		final double eps = desc.getEps();

		int a_start = r.nextInt(n);
		int b_start = r.nextInt(n);
		Gene a_center = new Gene(1, -1, Arrays.copyOf(neighborhood.get(a_start).data, samples));
		Gene b_center = new Gene(1, -1, Arrays.copyOf(neighborhood.get(b_start).data, samples));
		float[] a_center_pong = new float[samples];
		Arrays.fill(a_center_pong, Float.NaN);
		float[] b_center_pong = new float[samples];
		Arrays.fill(b_center_pong, Float.NaN);

		float[] tmp;
		BitSet partOf_a = new BitSet(n);

		double d_old = 0;
		for (int i = 0; i < maxit; ++i) {
			int j = 0;
			int changed = 0;
			double d_new = 0;
			for (Gene gene : neighborhood) {
				final double a_distance = distance(a_center, gene);
				final double b_distance = distance(b_center, gene);
				final boolean in_a = a_distance < b_distance;
				if (partOf_a.get(j) != in_a) {
					changed++;
					partOf_a.set(j, in_a);
				}
				d_new += in_a ? a_distance : b_distance;
				tmp = in_a ? a_center_pong : b_center_pong;
				// shift new center
				for (int k = 0; k < samples; ++k) {
					if (!gene.isNaN(k)) {
						if (Float.isNaN(tmp[k]))
							tmp[k] = gene.get(k);
						else
							tmp[k] += gene.get(k);
					}
				}
				j++;
			}
			if (changed == 0 || d_new == 0)
				break;
			final double ratio = Math.abs(d_new - d_old) / d_old;
			if (i > 0 && ratio < eps)
				break;
			d_old = d_new;
			int a_n = partOf_a.cardinality();
			int b_n = n - a_n;
			if (a_n == 0 || b_n == 0) {
				// FIXME
			}
			updateCenter(a_center, a_center_pong, a_n);
			updateCenter(b_center, b_center_pong, b_n);
		}

		return split(neighborhood, partOf_a);
	}

	private static Pair<List<Gene>, List<Gene>> split(List<Gene> neighborhood, BitSet partOf_a) {
		final int a_n = partOf_a.cardinality();
		final int b_n = neighborhood.size() - a_n;

		final List<Gene> a = new ArrayList<>(a_n);
		final List<Gene> b = new ArrayList<>(b_n);

		for (int i = 0; i < neighborhood.size(); ++i) {
			if (partOf_a.get(i))
				a.add(neighborhood.get(i));
			else
				b.add(neighborhood.get(i));
		}
		return Pair.make(a, b);
	}

	private void updateCenter(Gene center, float[] values, int n) {
		float[] data = center.data;
		for (int i = 0; i < samples; ++i)
			data[i] = values[i] / n;
		Arrays.fill(values, Float.NaN); // reset
	}

	private class ImputeKNNMeanImpl extends RecursiveAction {
		private static final long serialVersionUID = 1L;
		private final List<Gene> neighborhood;
		private final Gene gene;

		public ImputeKNNMeanImpl(List<Gene> neighborhood, Gene gene) {
			this.neighborhood = neighborhood;
			this.gene = gene;
		}

		@Override
		protected void compute() {
			TreeSet<NeighorGene> neighbors = new TreeSet<>();
			final int k = desc.getK();
			for (Gene other : neighborhood) {
				if (other == gene)
					continue;
				double distance = distance(gene, other);
				if (isInfinite(distance))
					continue;
				neighbors.add(new NeighorGene(other, distance));
				if (neighbors.size() > k)
					neighbors.pollLast();
			}
			for (int sample = 0; sample < samples; ++sample) {
				if (gene.isNaN(sample)) {
					double value = mean(neighbors, sample);
					if (Double.isNaN(value)) // This can fail if ALL the neighbors are missing in a particular element.
												// In
												// this case we use the overall column mean for that block of genes.
						value = getSample(sample).getMean();
					gene.setNextNaN(value);
				}
			}
		}
	}

	private double distance(Gene target, Gene neighbor) {
		double acc = 0;
		int n = 0;
		for (int sample = 0; sample < samples; ++sample) {
			if (target.isNaN(sample) || neighbor.isNaN(sample)) // skip missing
				continue;
			double dx = target.get(sample) - neighbor.get(sample);
			acc += dx * dx;
			n++;
		}
		if (n > 0) {
			return acc / n;// FIXME according to the fortran code, this is not the eucledian distance
			// return Math.sqrt(acc);
		}
		return Double.POSITIVE_INFINITY;
	}

	private Sample computeSample(final int sample) {
		int nans = 0;
		double sum = 0;
		int n = 0;
		for (Gene gene : genes) {
			double v = gene.get(sample);
			if (isNaN(v))
				nans++;
			else {
				sum += v;
				n++;
			}
		}
		return new Sample(sum / n, nans);
	}

	private static double mean(Iterable<NeighorGene> neighbors, int sample) {
		double sum = 0;
		int n = 0;
		for (NeighorGene gene : neighbors) {
			double v = gene.get(sample);
			if (!isNaN(v)) {
				sum += v;
				n++;
			}
		}
		return n == 0 ? Double.NaN : sum / n;
	}

	private class ImputeSampleMean extends RecursiveAction {
		private static final long serialVersionUID = 1L;
		private final Gene gene;

		public ImputeSampleMean(Gene gene) {
			this.gene = gene;
		}

		@Override
		protected void compute() {
			for (int sample = 0; sample < samples; ++sample) {
				if (gene.isNaN(sample))
					gene.setNextNaN(getSample(sample).getMean());
			}
		}
	}


	/**
	 * @param sample
	 * @return
	 */
	private Sample getSample(int sample) {
		return samples_.getUnchecked(sample);
	}

	/**
	 * @return
	 */
	private boolean toomanyNaNsInAColumn() {
		float colmax = desc.getColmax();
		if (Float.isInfinite(colmax) || Float.isNaN(colmax))
			return false;
		int max = Math.round(colmax * genes.size());
		for (int i = 0; i < samples; ++i) {
			int nans = getSample(i).getNans();
			if (nans > max)
				return true;
		}
		return false;
	}

	public static final class Gene {
		protected final int gene;
		// as we are working from left to right, we just store the replacements for each NaN
		private final float[] nanReplacements;
		private final float[] data;
		private int nanSetCounter = 0;

		public Gene(int gene, int nans, float[] data) {
			this.gene = gene;
			this.nanReplacements = nans < 0 ? null : new float[nans];
			this.data = data;
		}

		/**
		 * @param b
		 */
		public void fillImpute(Builder<Integer, Integer, Float> b) {
			if (!isAnySet())
				return;
			assert nanSetCounter == nanReplacements.length;
			int j = 0;
			for (int i = 0; i < data.length; ++i)
				if (Float.isNaN(data[i]))
					b.put(gene, i, nanReplacements[j++]);
		}

		public boolean isAnySet() {
			return nanSetCounter > 0;
		}

		/**
		 * @return
		 */
		public int size() {
			return data.length;
		}

		public int getNaNs() {
			return nanReplacements.length;
		}

		public void setNextNaN(double value) {
			nanReplacements[nanSetCounter++] = (float) value;
		}


		public boolean isNaN(int sample) {
			return Float.isNaN(data[sample]);
		}

		/**
		 * @param sample
		 * @return
		 */
		public float get(int sample) {
			return data[sample];
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Gene [gene=");
			builder.append(gene);
			builder.append(", nanSetCounter=");
			builder.append(nanSetCounter);
			builder.append(", nanReplacements=");
			builder.append(Arrays.toString(nanReplacements));
			builder.append("]");
			return builder.toString();
		}
	}

	private static final class NeighorGene implements Comparable<NeighorGene> {
		private final Gene gene;
		protected final double distance;

		public NeighorGene(Gene gene, double distance) {
			this.gene = gene;
			this.distance = distance;
		}

		public double get(int sample) {
			return gene.get(sample);
		}

		@Override
		public int compareTo(NeighorGene o) {
			int r = Double.compare(distance, o.distance);
			if (r == 0)
				return o.gene.gene - o.gene.gene;
			return r;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("NeighorGene [gene=");
			builder.append(gene);
			builder.append(", distance=");
			builder.append(distance);
			builder.append("]");
			return builder.toString();
		}

	}

	private static final class Sample {
		private final double mean;
		private final int nans;

		public Sample(double mean, int nans) {
			this.mean = mean;
			this.nans = nans;
		}

		public double getMean() {
			return mean;
		}

		public int getNans() {
			return nans;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Sample [mean=");
			builder.append(mean);
			builder.append(", nans=");
			builder.append(nans);
			builder.append("]");
			return builder.toString();
		}

	}

	public static void main(String[] args) throws IOException {
		ImmutableList.Builder<Gene> b = ImmutableList.builder();
		List<String> lines = CharStreams.readLines(new InputStreamReader(KNNImpute.class
				.getResourceAsStream("khan.csv")));
		lines = lines.subList(1, lines.size());
		int j = 0;
		for(String line : lines) {
			String[] l = line.split(";");
			float[] d = new float[l.length];
			int nans = 0;
			for(int i = 0; i < l.length; ++i) {
				if ("NA".equals(l[i])) {
					nans++;
					d[i] = Float.NaN;
				} else {
					d[i] = Float.parseFloat(l[i]);
				}
			}
			b.add(new Gene(j++, nans, d));
		}
		final KNNImputeDescription desc2 = new KNNImputeDescription();
		desc2.setMaxp(100000);
		KNNImpute r = new KNNImpute(desc2, b.build());
		ForkJoinPool p = new ForkJoinPool();
		p.invoke(r);
		try (PrintWriter w = new PrintWriter("khan.imputed.csv")) {
			w.println(StringUtils.repeat("sample", ";", r.samples));
			for (Gene g : r.genes) {
				float[] d = g.data;
				int nan = 0;
				w.print(Float.isNaN(d[0]) ? g.nanReplacements[nan++] : d[0]);
				for (int i = 1; i < d.length; ++i)
					w.append(';').append(String.valueOf(Float.isNaN(d[i]) ? g.nanReplacements[nan++] : d[i]));
				w.println();
			}
		}
	}
}
