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
package org.caleydo.view.tourguide.data.score;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.view.tourguide.data.BitSetIDSet;
import org.caleydo.view.tourguide.data.HashSetIDSet;
import org.caleydo.view.tourguide.data.IDSet;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * implementation of the jaccard score to compare groups
 *
 * @author Samuel Gratzl
 *
 */
public class JaccardIndexScore extends AGroupScore implements IBatchComputedGroupScore {
	private static final Logger log = Logger.create(JaccardIndexScore.class);

	private final IDSet bitSet;
	private final BitMapMapper toBitMap;

	public JaccardIndexScore(TablePerspective stratification, Group group) {
		this(null, stratification, group);
	}

	public JaccardIndexScore(String label, TablePerspective stratification, Group group) {
		super(label, stratification, group);
		toBitMap = new BitMapMapper(stratification.getRecordPerspective().getVirtualArray().getIdType());
		bitSet = createIds(new BitSetIDSet(), stratification.getRecordPerspective().getVirtualArray(), group);
	}

	@Override
	public void apply(Multimap<TablePerspective, Group> stratNGroups) {
		Iterable<Pair<Group, IDSet>> bitsets = transform(filter(stratNGroups.entries(), notInCache), toBitMap);
		for (Pair<Group, IDSet> entry : bitsets)
			put(entry.getFirst(), computeJaccardIndex(bitSet, entry.getSecond()));
	}

	@Override
	public void apply(Collection<IBatchComputedGroupScore> batch, Multimap<TablePerspective, Group> stratNGroups) {
		applyImpl(batch, stratNGroups);
	}

	private static void applyImpl(Collection<IBatchComputedGroupScore> batch,
			Multimap<TablePerspective, Group> stratNGroups) {
		Multimap<IDType, JaccardIndexScore> byIDCat = ArrayListMultimap.create();
		for (IBatchComputedGroupScore b : batch) {
			JaccardIndexScore s = (JaccardIndexScore) b;
			byIDCat.put(s.stratification.getRecordPerspective().getVirtualArray().getIdType(), s);
		}
		Set<TablePerspective> stratifications = stratNGroups.keySet();
		log.info("computing jaccard score for " + stratifications.size() + " stratifications, with total "
				+ stratNGroups.size() + " elements against: " + batch.size());
		if (Thread.interrupted()) // check if we should stop
			return;

		Collection<JaccardIndexScore> todo = new ArrayList<>();
		IDSet s = new HashSetIDSet();

		for (TablePerspective strat : stratifications) { // for each against
			if (Thread.interrupted())
				return;

			final RecordVirtualArray va = strat.getRecordPerspective().getVirtualArray();
			for (Group g : stratNGroups.get(strat)) { // for each group

				for (IDType targetType : byIDCat.keySet()) { // for each id type
					if (Thread.interrupted())
						return;

					IDMappingManager idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
							targetType.getIDCategory());

					todo.clear();
					// check what we really have to do
					for (JaccardIndexScore idElem : byIDCat.get(targetType)) {
						// everything in cache?
						if (!idElem.contains(strat, g))
							todo.add(idElem);
					}
					if (todo.isEmpty())
						continue;

					boolean doMapIds = !targetType.equals(va.getIdType());
					// create a bitset of this group

					s = doMapIds ? createMappedIds(s, va, g, idMappingManager, targetType) : createIds(s, va, g);
					// int sSize = s.cardinality();

					for (JaccardIndexScore idElem : todo) { // compute scores
						idElem.put(g, computeJaccardIndex(idElem.bitSet, s));
					}
				}
			}
		}
		log.info("done");
	}

	private Predicate<Map.Entry<TablePerspective, Group>> notInCache = new Predicate<Map.Entry<TablePerspective, Group>>() {
		@Override
		public boolean apply(Map.Entry<TablePerspective, Group> entry) {
			return !contains(entry.getKey(), entry.getValue());
		}
	};

	private static float computeJaccardIndex(IDSet aBits, IDSet bBits) {
		IDSet tmp;
		// iterate over those, which is faster
		if (aBits.isFastIteration() && bBits.isFastIteration()) {
			if (aBits.size() < bBits.size()) { // iterate over smaller
				tmp = aBits;
				aBits = bBits;
				bBits = tmp;
			}
		} else if (aBits.isFastIteration()) { // use the faster one
			tmp = aBits;
			aBits = bBits;
			bBits = tmp;
		}

		int intersection = 0;
		for (Integer b : bBits) {
			if (aBits.contains(b))
				intersection++;
		}
		int union = aBits.size() + bBits.size() - intersection;
		float score = union == 0 ? 0.f : (float) intersection / union;
		return score;
	}

	private static class BitMapMapper implements Function<Map.Entry<TablePerspective, Group>, Pair<Group, IDSet>> {
		private final IDType referenceIDType;
		private final IDMappingManager mappingManager;

		public BitMapMapper(IDType referenceIDType) {
			this.referenceIDType = referenceIDType;
			this.mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(referenceIDType.getIDCategory());
		}

		@Override
		public Pair<Group, IDSet> apply(Map.Entry<TablePerspective, Group> entry) {
			RecordVirtualArray va = entry.getKey().getRecordPerspective().getVirtualArray();
			Group group = entry.getValue();
			boolean doMapIds = !referenceIDType.equals(va.getIdType());
			IDSet s = doMapIds ? createMappedIds(new HashSetIDSet(), va, group, mappingManager, referenceIDType)
					: createIds(new HashSetIDSet(), va, group);
			return Pair.make(entry.getValue(), s);
		}
	}

	/**
	 * creates out of a group a bitset
	 *
	 * @param va
	 * @param group
	 * @return
	 */
	private static IDSet createIds(IDSet b, RecordVirtualArray va, Group group) {
		b.clear();
		for (int i = group.getStartIndex(); i <= group.getEndIndex(); ++i) {
			int id = va.get(i);
			b.set(id);
		}
		return b;
	}

	/**
	 * creates out of a group a bitset but transform the id using the provided {@link IDMappingManager}
	 *
	 * @param va
	 * @param group
	 * @param idMappingManager
	 * @param target
	 * @return
	 */
	private static IDSet createMappedIds(IDSet b, RecordVirtualArray va, Group group,
			IDMappingManager idMappingManager,
			IDType target) {
		b.clear();
		for (int i = group.getStartIndex(); i <= group.getEndIndex(); ++i) {
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
}
