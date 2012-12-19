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

import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.contextmenu.GroupContextMenuItem;
import org.caleydo.view.tourguide.algorithm.AdjustedRandIndex;
import org.caleydo.view.tourguide.algorithm.IGroupAlgorithm;
import org.caleydo.view.tourguide.algorithm.JaccardIndex;
import org.caleydo.view.tourguide.algorithm.LogRank;
import org.caleydo.view.tourguide.data.Scores;
import org.caleydo.view.tourguide.data.compute.ComputeScoreFilters;
import org.caleydo.view.tourguide.data.compute.IComputeScoreFilter;
import org.caleydo.view.tourguide.data.ui.CreateAdjustedRandScoreDialog;
import org.caleydo.view.tourguide.data.ui.CreateCompositeScoreDialog;
import org.caleydo.view.tourguide.data.ui.CreateJaccardIndexScoreDialog;
import org.caleydo.view.tourguide.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.event.CreateScoreColumnEvent;
import org.caleydo.view.tourguide.event.CreateScoreColumnEvent.Type;
import org.caleydo.view.tourguide.vendingmachine.ScoreQueryUI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author Samuel Gratzl
 *
 */
public class ScoreRegistry {

	public static IScore createAdjustedRand(String label, TablePerspective reference) {
		return new DefaultComputedStratificationScore(label, reference, AdjustedRandIndex.get(), null);
	}

	public static IGroupScore createJaccardScore(String label, TablePerspective reference, Group group, boolean mutualExclusive) {
		IComputeScoreFilter filter = mutualExclusive ? ComputeScoreFilters.MUTUAL_EXCLUSIVE : null;
		return new DefaultComputedReferenceGroupScore(label, reference, group, JaccardIndex.get(), filter);
	}

	public static IScore createLogRankGroupMetric(String label, Integer clinicalVariable) {
		final ATableBasedDataDomain clinical = DataDomainOracle.getClinicalDataDomain();
		final IGroupAlgorithm underlying = LogRank.get(clinicalVariable, clinical);
		final IGroupAlgorithm al = new IGroupAlgorithm() {
			@Override
			public IDType getTargetType(TablePerspective a, TablePerspective b) {
				return underlying.getTargetType(a, b);
			}

			@Override
			public String getAbbreviation() {
				return underlying.getAbbreviation();
			}

			@Override
			public float compute(Set<Integer> a, Set<Integer> b) {
				// me versus the rest
				return underlying.compute(a, Sets.difference(b, a));
			}
		};
		return new DefaultComputedGroupScore(label, al, null);
	}



	public static void addCreateScoreItems(ContextMenuCreator creator, Set<IScore> visible, ScoreQueryUI sender) {
		creator.addContextMenuItem(new GenericContextMenuItem("Create Jaccard Index Score", new CreateScoreColumnEvent(
				CreateScoreColumnEvent.Type.JACCARD, sender)));
		creator.addContextMenuItem(new GenericContextMenuItem("Create Adjusted Rand Score", new CreateScoreColumnEvent(
				CreateScoreColumnEvent.Type.ADJUSTED_RAND, sender)));
	}

	public static void addCreateCombinedItems(ContextMenuCreator creator, Set<IScore> visible, ScoreQueryUI sender) {
		creator.addContextMenuItem(new GenericContextMenuItem("Create Combined Score", new CreateScoreColumnEvent(
				CreateScoreColumnEvent.Type.COMBINED, sender)));
		creator.addContextMenuItem(new GenericContextMenuItem("Create Collapsed Score", new CreateScoreColumnEvent(
				CreateScoreColumnEvent.Type.COLLAPSED, sender)));
	}

	/**
	 * adds the context menu items for creating metrics
	 *
	 * @param creator
	 * @param visible
	 * @param sender
	 */
	public static void addCreateMetricItems(ContextMenuCreator creator, Set<IScore> visible, ScoreQueryUI sender) {
		if (!visible.contains(SizeMetric.get()))
			creator.addContextMenuItem(new GenericContextMenuItem("Add Size Metric", new AddScoreColumnEvent(SizeMetric
					.get(), sender)));

		Iterable<IScore> logRankScores = Iterables.filter(Iterables.transform(DataDomainOracle.getClinicalVariables(),
				new Function<Pair<Integer, String>, IScore>() {
					@Override
					public IScore apply(Pair<Integer, String> p) {
						return ScoreRegistry.createLogRankGroupMetric(p.getSecond(), p.getFirst());
					}
				}), Predicates.not(Predicates.in(visible)));

		GroupContextMenuItem logRanks = new GroupContextMenuItem("Create LogRank of");
		boolean hasOne = false;
		for (IScore score : logRankScores) {
			hasOne = true;
			logRanks.add(new GenericContextMenuItem(score.getLabel(), new AddScoreColumnEvent(score, sender)));
		}
		if (hasOne)
			creator.addContextMenuItem(logRanks);
	}


	/**
	 * handles the event for a create context menu item event and returns the corresponding dialog
	 *
	 * @param type
	 * @param shell
	 * @param scoreQueryUI
	 * @return
	 */
	public static Dialog createCreateDialog(Type type, Shell shell, ScoreQueryUI scoreQueryUI) {
		switch (type) {
		case COMBINED:
			return new CreateCompositeScoreDialog(shell, Scores.get().getScoreIDs(), scoreQueryUI, false);
		case COLLAPSED:
			return new CreateCompositeScoreDialog(shell, Scores.get().getScoreIDs(), scoreQueryUI, true);
		case JACCARD:
			return new CreateJaccardIndexScoreDialog(shell, scoreQueryUI);
		case ADJUSTED_RAND:
			return new CreateAdjustedRandScoreDialog(shell, scoreQueryUI);
		default:
			throw new IllegalStateException();
		}
	}
}

