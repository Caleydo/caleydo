/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.impl;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.ChiSquaredDistribution;
import org.apache.commons.math.distribution.ChiSquaredDistributionImpl;
import org.caleydo.core.util.logging.Logger;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

/**
 * util set of statistics functions
 *
 * @author Samuel Gratzl
 *
 */
public final class Statistics {
	private static final Logger log = Logger.create(Statistics.class);

	private Statistics() {

	}

	/**
	 * computes the rand index, see <a href="http://en.wikipedia.org/wiki/Rand_index">Rand_Index</a> in Wikipedia
	 *
	 * @param x
	 *            the collection of groups of the first stratification
	 * @param y
	 *            the collection of groups of the second stratification
	 * @return its rand index
	 */
	public static float randIndex(Collection<? extends Collection<Integer>> as,
			Collection<? extends Collection<Integer>> bs) {
		List<Integer> alist = new ArrayList<>();
		List<Integer> blist = new ArrayList<>();
		int ai = 0;
		for (Collection<Integer> ag : as) {
			for (Integer id : ag) {
				// search in which this id is in the other stratification
				int bi = 0;
				for (Collection<Integer> bg : bs) {
					if (bg.contains(id)) {
						alist.add(ai);
						blist.add(bi);
						break;
					}
					bi++;
				}
			}
			ai++;
		}
		final int[] x = Ints.toArray(alist);
		final int[] y = Ints.toArray(blist);
		final int nn = x.length;

		// a, the number of pairs of elements in S that are in the same set in X and in the same set in Y
		int a = 0;
		// b, the number of pairs of elements in S that are in different sets in X and in different sets in Y+
		int b = 0;
		// c, the number of pairs of elements in S that are in the same set in X and in different sets in Y
		int c = 0;
		// d, the number of pairs of elements in S that are in different sets in X and in the same set in Y
		int d = 0;

		for (int t = 0; t < nn; t++) {
			for (int r = t + 1; r < nn; r++) {
				final boolean sameX = x[t] == x[r];
				final boolean sameY = y[t] == y[r];
				if (sameX) {
					if (sameY)
						a++;
					else
						c++;
				} else {
					if (sameY)
						d++;
					else
						b++;
				}
			}
		}
		// R = \frac{a+b}{a+b+c+d} = \frac{a+b}{{n \choose 2 }}
		float r = (a + b) / (float) (a + b + c + d);

		return r;
	}

	/**
	 * computes the adjusted rand, see <a href="http://en.wikipedia.org/wiki/Rand_index">Rand_Index</a> in Wikipedia
	 *
	 * @param x
	 *            the collection of groups of the first stratification
	 * @param y
	 *            the collection of groups of the second stratification
	 * @return its adjusted rand
	 */
	public static float adjustedRand(Collection<Set<Integer>> x, Collection<Set<Integer>> y) {
		// http://en.wikipedia.org/wiki/Rand_index
		// ARI = \frac{ \sum_{ij} \binom{n_{ij}}{2} - [\sum_i \binom{a_i}{2} \sum_j \binom{b_j}{2}] / \binom{n}{2} }{
		// \frac{1}{2} [\sum_i \binom{a_i}{2} + \sum_j \binom{b_j}{2}] - [\sum_i \binom{a_i}{2} \sum_j \binom{b_j}{2}] /
		// \binom{n}{2} }
		// where n_{ij}, a_i, b_j are values from the contingency table.

		int n = 0;
		int ais = 0;
		int bjs = 0;
		int nijs = 0;

		int bj[] = new int[y.size()];
		Arrays.fill(bj, 0);

		for (Set<Integer> xi : x) {
			int ai = 0;
			int j = 0;
			for (Set<Integer> yi : y) {
				int nij = Sets.intersection(xi, yi).size();
				ai += nij;
				nijs += binom2(nij);
				bj[j] += nij;
				j++;
			}
			ais += binom2(ai);
			n += xi.size();
		}
		for (int j = 0; j < bj.length; j++)
			bjs += binom2(bj[j]);

		float n2 = 1.f / binom2(n);

		float ari = (nijs - (ais * bjs) * n2) / (0.5f * (ais + bjs) - (ais * bjs) * n2);

		return ari;
	}

	/**
	 * computes the jaccard index between the two sets
	 *
	 * @param a
	 *            the ids of the first set
	 * @param b
	 *            the ids of the second set
	 * @return its jaccard index
	 */
	public static double jaccardIndex(Collection<Integer> a, Collection<Integer> b) {
		int intersection = 0;
		for (Integer ai : a) {
			if (b.contains(ai))
				intersection++;
		}
		int union = b.size() + a.size() - intersection;
		// intersect(a,b) / union(a,b)
		double score = union == 0 ? 0. : (double) intersection / union;
		return score;
	}

	// cache for most used one
	private static transient SoftReference<ChiSquaredDistribution> chiSquare1 = null;

	public static double chiSquaredProbability(double x, int df) {
		// return weka.core.Statistics.chiSquaredProbability(x, df);
		ChiSquaredDistribution d;
		if (df == 1) {
			d = chiSquare1 != null ? chiSquare1.get() : null;
			if (d == null) {
				d = new ChiSquaredDistributionImpl(1);
				chiSquare1 = new SoftReference<>(d);
			}
		} else {
			d = new ChiSquaredDistributionImpl(df);
		}
		try {
			return 1.0 - d.cumulativeProbability(x);
		} catch (MathException e) {
			log.error("can't compute chiSquaredProbability of " + x + " with df: " + df, e);
		}
		return Float.NaN;
	}

	// public static double tTest(double mu, double[] observed) {
	// try {
	// return TestUtils.tTest(mu, observed);
	// } catch (IllegalArgumentException | MathException e) {
	// log.error("can't compute tTest of " + Arrays.toString(observed) + " with mu: " + mu, e);
	// }
	// return Float.NaN;
	// }
	//
	// public static double tTest(double[] a, double[] b) {
	// try {
	// return TestUtils.tTest(a, b);
	// } catch (IllegalArgumentException | MathException e) {
	// log.error("can't compute tTest of " + Arrays.toString(a) + " and " + Arrays.toString(b), e);
	// }
	// return Float.NaN;
	// }

	public static float foldChange(float a, float b) {
		if (a > b)
			return a / b;
		else
			return -b / a;
	}

	/**
	 * computes the log rank score between the two given survival curves
	 *
	 * based on <a href="http://www.ncbi.nlm.nih.gov/pmc/articles/PMC403858/">Logrank_test</a>
	 *
	 * @param as
	 *            the survival of specific samples of a
	 * @param asurvived
	 *            the number of survived samples after the experiment
	 * @param bs
	 *            the survival of specific samples of b
	 * @param bsurvived
	 *            the number of survived samples in b after the experiment
	 * @return
	 */
	public static double logRank(List<Double> as_l, int asurvived, List<Double> bs_l, int bsurvived) {
		final double[] as = sorted(as_l);
		final double[] bs = sorted(bs_l);

		// distinct events
		final SortedSet<Double> distinct = ImmutableSortedSet.<Double> naturalOrder().addAll(as_l).addAll(bs_l).build();
		int a_i = 0, b_i = 0;

		double a_deaths_expected_acc = 0;
		double a_deaths_variance_acc = 0;
		int a_deaths_observed_acc = 0;

		double b_deaths_expected_acc = 0;
		double b_deaths_variance_acc = 0;
		int b_deaths_observed_acc = 0;

		for (double event : distinct) {

			int a_alive = asurvived + as.length - a_i; // alive before the event
			int a_deaths = deathsAt(as, a_i, event); // find deaths
			a_i += a_deaths; // shift event consumption

			int b_alive = bsurvived + bs.length - b_i;
			int b_deaths = deathsAt(bs, b_i, event);
			b_i += b_deaths; // shift

			double deaths = a_deaths + b_deaths;
			double alive = a_alive + b_alive;

			double a_deaths_expected = expected(a_alive, deaths, alive);
			a_deaths_expected_acc += a_deaths_expected;
			a_deaths_observed_acc += a_deaths;

			double b_deaths_expected = expected(b_alive, deaths, alive);
			b_deaths_expected_acc += b_deaths_expected;
			b_deaths_observed_acc += b_deaths;

			double a_deaths_variance = var(a_alive, deaths, alive);
			a_deaths_variance_acc += a_deaths_variance;

			double b_deaths_variance = var(b_alive, deaths, alive);
			b_deaths_variance_acc += b_deaths_variance;
		}

		// double z_article = Math.pow(a_deaths_observed_acc - a_deaths_expected_acc, 2) / a_deaths_expected_acc +
		// Math.pow(b_deaths_observed_acc - b_deaths_expected_acc, 2) / b_deaths_expected_acc;
		// //http://www.ncbi.nlm.nih.gov/pmc/articles/PMC403858/
		// double z_wiki = (b_deaths_observed_acc - b_deaths_expected_acc) / Math.sqrt(b_deaths_variance_acc);
		// //wikipedia

		double z_r = Math.pow(b_deaths_observed_acc - b_deaths_expected_acc, 2) / b_deaths_variance_acc; // r survival
		// double z_r2 = Math.pow(a_deaths_observed_acc - a_deaths_expected_acc, 2) / a_deaths_variance_acc; // r
		// survival
		// // package
		return z_r;
	}

	private static double expected(int a_alive, double deaths, double alive) {
		return a_alive == 0 ? 0 : (deaths / alive) * a_alive;
	}

	private static double var(int a_alive, double deaths, double alive) {
		return a_alive == 0 || alive == 1 ? 0 : (deaths * (a_alive / alive)
				* (1 - a_alive / alive) * (alive - deaths))
				/ (alive - 1);
	}

	private static int deathsAt(final double[] as, int start, double event) {
		int deaths = 0;
		for (int i = start; i < as.length && as[i] == event; ++i) { // event hits
			deaths++; // find act
		}
		return deaths;
	}

	private static double[] sorted(List<Double> l) {
		final double[] as = Doubles.toArray(l);
		Arrays.sort(as);
		return as;
	}

	private static int binom2(int n) {
		return (n - 1) * n / 2;
	}

	public static void main(String[] args) {
		{
			List<Double> a = Doubles.asList(41, 54, 59, 65, 67, 73, 92, 137, 139, 164, 220, 224, 245, 311, 329, 330,
					333,
					336, 342, 362, 431, 454, 459, 477, 479, 480, 485, 510, 551, 563, 572, 573, 586, 637, 678, 683, 709,
					722, 768, 769, 782, 822, 827, 877, 884, 927, 945, 951, 992, 1092, 1097, 1121, 1173, 1190, 1316,
					1337, 1370, 1417, 1463, 1587, 1590, 1597, 1625, 1661, 1723, 1912, 1913, 1964, 1979, 2145, 2190,
					2227, 2298, 2751, 2763);
			int asurvived = 217;
			List<Double> b = Doubles.asList(50);
			int bsurvived = 0;

			double z = logRank(a, asurvived, b, bsurvived);
			double p = chiSquaredProbability(z, 1);
			System.out.println("test1: " + z + " " + p);
		}

		{
//			 a =59  115  156  431  448  477  638  803  855 1040 1106  268  329);
//			 as = 1 1 1 1 0 0 1 0 0 0 0 1 1;
//			 b = 421  464  475  563  744  769  770 1129 1206 1227  353  365  377);
//			 bs = 0 1 1 1 0 0 0 0 0 0 1 1 0;
			List<Double> a2 = Doubles.asList(59, 115, 156, 431, 638, 268, 329);
			Collections.sort(a2);
			int asurv = 6;
			List<Double> b2 = Doubles.asList(464, 475, 563, 353, 365);
			int bsurv = 8;
			Collections.sort(b2);
			double z = logRank(a2, asurv, b2, bsurv);
			double p = chiSquaredProbability(z, 1);
			System.out.println("ovarian: " + z + " " + p);
		}

	}
}
