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
package org.caleydo.core.data.perspective.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.collection.Triple;

/**
 * Jaccard index for comparing individual clusters. See:
 * http://en.wikipedia.org/wiki/Jaccard_index
 *
 * @author Marc Streit
 */
public class JaccardIndex {

	/**
	 * The table perspective to which the score belongs to.
	 */
	private final TablePerspective base;

	private final Map<TablePerspective, JaccardIndexScores> cache = new HashMap<TablePerspective, JaccardIndexScores>();

	public JaccardIndex(TablePerspective referenceTablePerspective) {
		this.base = referenceTablePerspective;
	}

	/**
	 * maps the perspectives in A as a whole to the groups within all Bs
	 *
	 * @param a
	 * @param b
	 * @return list of triple containing with A,B,ClusterScore
	 */
	public static Iterable<Triple<TablePerspective, TablePerspective, JaccardIndexScores>> createScores(final Collection<TablePerspective> a, final Collection<TablePerspective> b) {

		long start = System.currentTimeMillis();
		// create a list of id caches where we test against
		List<TableIds> bIds = new ArrayList<JaccardIndex.TableIds>(b.size());
		for(TablePerspective against : b) {
			final List<TablePerspective> groups = against.getRecordSubTablePerspectives();
			// Ingore the ungrouped default
			if (groups.size() == 1)
				continue;
			bIds.add(new TableIds(against, groups));
		}

		List<Triple<TablePerspective, TablePerspective, JaccardIndexScores>> scores = new ArrayList<Triple<TablePerspective, TablePerspective, JaccardIndexScores>>(bIds.size() * a.size());

		for (TablePerspective referenceTablePerspective : a) {
			JaccardIndex index = referenceTablePerspective.getContainerStatistics().getJaccardIndex();
			Collection<Triple<TablePerspective, TablePerspective, JaccardIndexScores>> partial = index.getScore(bIds);
			System.out.println("compute scores of "+referenceTablePerspective.getLabel()+" with "+partial.size()+" elements");
			scores.addAll(partial);
		}
		System.out.println("time needed: " + (System.currentTimeMillis() - start) + "ms");
		return scores;
	}


	private Collection<Triple<TablePerspective, TablePerspective, JaccardIndexScores>> getScore(List<TableIds> bIds) {
		Collection<Triple<TablePerspective, TablePerspective, JaccardIndexScores>> result = new ArrayList<Triple<TablePerspective, TablePerspective, JaccardIndexScores>>(bIds.size());

		RecordVirtualArray referenceVA = base.getRecordPerspective().getVirtualArray();
		BitSet referenceIds = createIds(referenceVA, base.getRecordGroup());

		for (TableIds bId : bIds) {
			// check if calculation result is already available
			if (cache.containsKey(bId.base))
				result.add(Triple.make(base, bId.base, cache.get(bId.base)));
			else {
				JaccardIndexScores r = bId.create(referenceIds, referenceVA.getIdType());
				cache.put(bId.base, r);
				result.add(Triple.make(base, bId.base, r));
			}
		}
		return result;
	}

	/**
	 *
	 * @param tablePerspective The table perspective to compare
	 * @return the calculation result
	 */
	public JaccardIndexScores getScore(TablePerspective tablePerspective, boolean storeResult) {
		// check if calculation result is already available
		if (cache.containsKey(tablePerspective))
			return cache.get(tablePerspective);
		final List<TablePerspective> groups = tablePerspective.getRecordSubTablePerspectives();
		// Ingore the ungrouped default
		if (groups.size() == 1)
			return new JaccardIndexScores();

		Triple<TablePerspective, TablePerspective, JaccardIndexScores> data = getScore(Arrays.asList(new TableIds(tablePerspective, groups))).iterator().next();

		return data.getThird();
	}


	private static BitSet createIds(RecordVirtualArray va, Group group) {
		BitSet b = new BitSet();
		for (int i = 0; i < group.getSize(); ++i) {
			int id = va.get(i);

			b.set(id);
		}
		return b;
	}

	private static BitSet createMappedIds(RecordVirtualArray va, Group group, IDMappingManager idMappingManager, IDType target) {
		BitSet b = new BitSet();
		for (int i = 0; i < group.getSize(); ++i) {
			int id = va.get(i);
			Set<Integer> ids = idMappingManager.getIDAsSet(va.getIdType(), target, id);
			if (ids != null) {
				id = ids.iterator().next();
				if (ids.size() > 2) {
					System.out.println("Multi-Mapping");
				}
			}
			b.set(id);
		}
		return b;
	}

	private static boolean skipGroup(Group subGroup) {
		return subGroup.getSize() == 0 || subGroup.getLabel().equals("Not Mutated") || subGroup.getLabel().equals("Normal");
	}

	/**
	 * native pair of TablePerspective to score, to avoid that unboxing using maps
	 * @author Samuel Gratzl
	 *
	 */
	public static final class JaccardIndexScorePair {
		private final TablePerspective first;
		private final float second;

		public JaccardIndexScorePair(TablePerspective first, float second) {
			this.first = first;
			this.second = second;
		}

		public TablePerspective getFirst() {
			return first;
		}

		public float getSecond() {
			return second;
		}
	}

	/**
	 * shortcut for better readability
	 * @author Samuel Gratzl
	 *
	 */
	public static final class JaccardIndexScores extends ArrayList<JaccardIndexScorePair> {
		private static final long serialVersionUID = -8200252499106378750L;
	}

	/**
	 * cache bitsets per id type for each cluster
	 * @author Samuel Gratzl
	 *
	 */
	private static class TableIds {
		private final TablePerspective base;
		// map from idtype to cluster bitsets using a concurrent map for multi threading support
		private final ConcurrentMap<IDType, List<Pair<TablePerspective, BitSet>>> cache = new ConcurrentHashMap<IDType, List<Pair<TablePerspective, BitSet>>>();
		private final List<TablePerspective> groups;

		public TableIds(TablePerspective base, List<TablePerspective> groups) {
			this.base = base;
			this.groups = groups;
		}

		public JaccardIndexScores create(BitSet referenceIds, IDType referenceIDType) {
			if (!cache.containsKey(referenceIDType)) {
				cache.putIfAbsent(referenceIDType, create(referenceIDType));
			}

			// create the scores
			List<Pair<TablePerspective, BitSet>> entry = cache.get(referenceIDType);
			JaccardIndexScores r = new JaccardIndexScores();
			final int sizeA = referenceIds.cardinality();

			for (Pair<TablePerspective, BitSet> pair : entry) {
				BitSet tmp = (BitSet) pair.getSecond().clone(); // local copy
				int sizeB = tmp.cardinality();
				tmp.and(referenceIds);
				int intersection = tmp.cardinality();
				int union = sizeA + sizeB - intersection;
				float score = union == 0 ? 0.f : (float) intersection / union;

				r.add(new JaccardIndexScorePair(pair.getFirst(), score));
			}
			r.trimToSize();
			return r;
		}

		private List<Pair<TablePerspective, BitSet>> create(IDType referenceIDType) {
			final RecordVirtualArray va = base.getRecordPerspective().getVirtualArray();

			boolean doMapIds = referenceIDType != va.getIdType();
			final IDMappingManager idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(referenceIDType.getIDCategory());

			List<Pair<TablePerspective,BitSet>> result = new ArrayList<Pair<TablePerspective,BitSet>>(groups.size());

			for (TablePerspective subTablePerspective : groups) {
				Group subGroup = subTablePerspective.getRecordGroup();
				if (skipGroup(subGroup))
					continue;
				BitSet subGroupIds = doMapIds ? createMappedIds(va, subGroup, idMappingManager, referenceIDType) : createIds(va, subGroup);
				result.add(Pair.make(subTablePerspective,  subGroupIds));
			}
			return result;
		}
	}
}
