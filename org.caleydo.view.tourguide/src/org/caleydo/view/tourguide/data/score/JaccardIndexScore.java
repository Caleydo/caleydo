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
import java.util.BitSet;
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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Samuel Gratzl
 *
 */
public class JaccardIndexScore extends AGroupScore implements IBatchComputedGroupScore {
	private static final Logger log = Logger.create(JaccardIndexScore.class);
	private final BitSet bitSet;
	private final BitMapMapper toBitMap;

	public JaccardIndexScore(TablePerspective stratification, Group group) {
		super(stratification, group);
		toBitMap = new BitMapMapper(stratification.getRecordPerspective().getVirtualArray().getIdType());
		bitSet = createIds(stratification.getRecordPerspective().getVirtualArray(), group);
	}

	@Override
	public void apply(Multimap<TablePerspective, Group> stratNGroups) {
		Iterable<Pair<Group, BitSet>> bitsets = transform(filter(stratNGroups.entries(), notInCache), toBitMap);
		for (Pair<Group, BitSet> entry : bitsets)
			put(entry.getFirst(), computeJaccardIndex(group, bitSet, entry.getFirst(), entry.getSecond()));
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
		int i = 0;
		for (TablePerspective strat : stratifications) { // for each against
			// log.info("next: strat " + (i++) + " " + strat.getLabel());
			RecordVirtualArray va = strat.getRecordPerspective().getVirtualArray();

			for (Group g : stratNGroups.get(strat)) { // for each group

				for (IDType targetType : byIDCat.keySet()) { // for each id type
					IDMappingManager idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
							targetType.getIDCategory());

					// check what we really have to do
					Collection<JaccardIndexScore> todo = new ArrayList<>();
					for (JaccardIndexScore idElem : byIDCat.get(targetType)) {
						// everything in cache?
						// if (!idElem.contains(strat, g))
							todo.add(idElem);
					}

					if (todo.isEmpty())
						continue;

					boolean doMapIds = !targetType.equals(va.getIdType());
					// create a bitset of this group
					BitSet s = doMapIds ? createMappedIds(va, g, idMappingManager, targetType) : createIds(va, g);

					for (JaccardIndexScore idElem : todo) { // compute scores
						idElem.put(g, computeJaccardIndex(idElem.group, idElem.bitSet, g, s));
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

	private static float computeJaccardIndex(Group a, BitSet aBits, Group b, BitSet bBits) {
		BitSet tmp = (BitSet) bBits.clone(); // local copy
		tmp.and(aBits);
		int intersection = tmp.cardinality();
		int union = aBits.cardinality() + bBits.cardinality() - intersection;
		float score = union == 0 ? 0.f : (float) intersection / union;
		return score;
	}

	private static class BitMapMapper implements Function<Map.Entry<TablePerspective, Group>, Pair<Group, BitSet>> {
		private final IDType referenceIDType;
		private final IDMappingManager mappingManager;

		public BitMapMapper(IDType referenceIDType) {
			this.referenceIDType = referenceIDType;
			this.mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(referenceIDType.getIDCategory());
		}

		@Override
		public Pair<Group, BitSet> apply(Map.Entry<TablePerspective, Group> entry) {
			RecordVirtualArray va = entry.getKey().getRecordPerspective().getVirtualArray();
			Group group = entry.getValue();
			boolean doMapIds = !referenceIDType.equals(va.getIdType());
			BitSet s = doMapIds ? createMappedIds(va, group, mappingManager, referenceIDType) : createIds(va, group);
			return Pair.make(entry.getValue(), s);
		}
	}

	private static BitSet createIds(RecordVirtualArray va, Group group) {
		BitSet b = new BitSet();
		for (int i = group.getStartIndex(); i <= group.getEndIndex(); ++i) {
			int id = va.get(i);
			b.set(id);
		}
		return b;
	}

	private static BitSet createMappedIds(RecordVirtualArray va, Group group, IDMappingManager idMappingManager,
			IDType target) {
		BitSet b = new BitSet();
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
