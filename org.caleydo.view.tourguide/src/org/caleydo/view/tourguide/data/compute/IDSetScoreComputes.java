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
package org.caleydo.view.tourguide.data.compute;

import java.util.HashMap;
import java.util.Map;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.view.tourguide.data.score.AGroupScore;
import org.caleydo.view.tourguide.util.Grouper;

import com.google.common.base.Predicate;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Multimap;

/**
 * helper command to perform a computation on a group by group base, e.g. jaccard index
 *
 * @author Samuel Gratzl
 *
 */
public class IDSetScoreComputes implements Runnable {
	private static final Logger log = Logger.create(IDSetScoreComputes.class);
	private final Map<Pair<Group, IDType>, IDSet> sets = new HashMap<>();
	private final Map<Pair<IDType, IDType>, IIDTypeMapper<Integer, Integer>> mappers = new HashMap<>();
	private final IIDSetGroupScoreFun fun;
	private final Multimap<TablePerspective, Group> a;
	private final Multimap<TablePerspective, AGroupScore> b;

	public IDSetScoreComputes(Multimap<TablePerspective, Group> a,
			Multimap<TablePerspective, AGroupScore> b, IIDSetGroupScoreFun fun) {
		this.fun = fun;
		this.a = a;
		this.b = b;
	}

	@Override
	public void run() {
		if (a.isEmpty() || b.isEmpty())
			return;


		Multimap<IDType, TablePerspective> as = Grouper.byIDType(a.keySet());
		Multimap<IDType, TablePerspective> bs = Grouper.byIDType(b.keySet());

		log.info("computing group similarity of " + a.size() + " against " + b.size() + " others");
		Stopwatch w = new Stopwatch().start();

		for (IDType targetType : as.keySet()) {

			for (IDType sourceType : bs.keySet()) {

				if (!targetType.getIDCategory().equals(sourceType.getIDCategory())) {
					// can't map
					continue;
				}
				final Predicate<Integer> target2Source = in(targetType, sourceType);

				for (TablePerspective targetStrat : as.get(targetType)) {
					for (TablePerspective sourceStrat : bs.get(sourceType)) {
						for (Group targetGroup : a.get(targetStrat)) {
							for (AGroupScore sourceGroup : b.get(sourceStrat)) {
								if (sourceGroup.contains(targetStrat, targetGroup)) // cached
									continue;
								IDSet aSet = get(targetStrat, targetGroup, targetType); // in target notation
								IDSet bSet = get(sourceStrat, sourceGroup.getGroup(), targetType); // in target notation
								sourceGroup.put(targetGroup, fun.apply(aSet, bSet, target2Source));
							}
						}
					}

					// cleanup cache
					for (Group targetGroup : a.get(targetStrat)) {
						sets.remove(targetGroup);
					}
				}
			}
		}
		System.out.println("done in " + w);
	}

	private Predicate<Integer> in(final IDType source, final IDType target) {
		final Map<Integer, Boolean> cache = new HashMap<>();
		return new Predicate<Integer>() {
			@Override
			public boolean apply(Integer sourceId) {
				if (cache.containsKey(sourceId)) {
					return cache.get(sourceId);
				}
				IIDTypeMapper<Integer, Integer> mapper = getMapper(source, target);
				if (mapper == null)
					return false;
				boolean r = mapper.isMapAble(sourceId);
				cache.put(sourceId, r);
				return r;
			}
		};
	}

	private IIDTypeMapper<Integer, Integer> getMapper(IDType source, IDType target) {
		// find way
		IIDTypeMapper<Integer, Integer> mapper = mappers.get(Pair.make(source, target));
		if (mapper == null) {
			mapper = IDMappingManagerRegistry.get().getIDMappingManager(target.getIDCategory())
						.getIDTypeMapper(source, target);
			mappers.put(Pair.make(source, target), mapper);
		}
		return mapper;
	}

	private IDSet get(TablePerspective strat, Group group, IDType target) {
		if (sets.containsKey(Pair.make(group, target)))
			return sets.get(Pair.make(group, target));

		IDType source = strat.getRecordPerspective().getIdType();

		IIDTypeMapper<Integer, Integer> mapper = getMapper(source, target);
		if (mapper == null)
			return null;

		RecordVirtualArray va = strat.getRecordPerspective().getVirtualArray();
		IDSet b = new HashSetIDSet(mapper.apply(va.getIDsOfGroup(group.getGroupIndex())));

		sets.put(Pair.make(group, target), b);
		return b;
	}

	public interface IIDSetGroupScoreFun {
		/**
		 * applies an operation given the IDSet of A, B, denoted in the IDType of A and a predicate whether an id of A
		 * can be converted to B
		 */
		float apply(IDSet a, IDSet b, Predicate<Integer> a2B);
	}
}
