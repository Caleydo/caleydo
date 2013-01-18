/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.util.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.caleydo.core.util.logging.Logger;

import com.google.common.collect.Sets;
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
	public static float jaccardIndex(Collection<Integer> a, Collection<Integer> b) {
		int intersection = 0;
		for (Integer ai : a) {
			if (b.contains(ai))
				intersection++;
		}
		int union = b.size() + a.size() - intersection;

		float score = union == 0 ? 0.f : (float) intersection / union;
		return score;
	}

	// cache for most used one
	// private static transient SoftReference<ChiSquaredDistribution> chiSquare1 = null;

	public static double chiSquaredProbability(double x, int df) {

		return weka.core.Statistics.chiSquaredProbability(x, df);
		// ChiSquaredDistribution d;
		// if (df == 1) {
		// d = chiSquare1 != null ? chiSquare1.get() : null;
		// if (d == null) {
		// d = new ChiSquaredDistributionImpl(1);
		// chiSquare1 = new SoftReference<>(d);
		// }
		// } else {
		// d = new ChiSquaredDistributionImpl(df);
		// }
		// try {
		// return 1.0 - d.cumulativeProbability(x);
		// } catch (MathException e) {
		// log.error("can't compute chiSquaredProbability of " + x + " with df: " + df, e);
		// }
		// return Float.NaN;
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
	 * based on <a href="http://en.wikipedia.org/wiki/Logrank_test">Logrank_test</a> and the book: Survival Analysis: A
	 * Self-Learning Text
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
	public static float logRank(List<Float> as, int asurvived, List<Float> bs, int bsurvived) {
		SortedSet<Float> distinct = new TreeSet<>(as);
		distinct.addAll(bs);
		int ai = 0, bi = 0;

		float nom = 0, denom = 0;

		for (float j : distinct) {
			// 1
			float o1j = 0;
			while (ai < as.size() && as.get(ai) == j) {
				o1j++; // find act
				ai++;
			}
			float n1j = as.size() + asurvived - ai; // rest
			// 2
			float o2j = 0;
			while (bi < bs.size() && bs.get(bi) == j) {
				o2j++; // find act
				bi++;
			}
			float n2j = bs.size() + bsurvived - bi; // rest

			float e1j = n1j == 0 ? 0 : (o1j + o2j) * n1j / (n1j + n2j);
			float vj = (n1j == 0 || n2j == 0) ? 0 : (n1j * n2j * (o1j + o2j) * (n1j + n2j - o1j - o2j))
					/ ((n1j + n2j) * (n1j + n2j) * (n1j + n2j - 1));

			nom += o1j - e1j;
			denom += vj;
		}
		float z = (nom * nom) / denom;
		return z;
	}

	private static int binom2(int n) {
		return (n - 1) * n / 2;
	}
}
