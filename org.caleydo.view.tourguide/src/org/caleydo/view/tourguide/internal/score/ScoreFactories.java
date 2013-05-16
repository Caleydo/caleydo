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
package org.caleydo.view.tourguide.internal.score;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.view.tourguide.api.state.IStateMachine;
import org.caleydo.view.tourguide.internal.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.spi.IScoreFactory;
import org.caleydo.view.tourguide.spi.IScoreFactory.ScoreEntry;

import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
public class ScoreFactories {
	private static final String EXTENSION_ID = "org.caleydo.tourguide.scorefactory";

	private final static Map<String, IScoreFactory> factories;

	static {
		factories = ExtensionUtils.findImplementation(EXTENSION_ID, "name", "class", IScoreFactory.class);
	}

	public static Map<String, IScoreFactory> getFactories() {
		return Collections.unmodifiableMap(factories);
	}

	public static Iterable<AContextMenuItem> createGroupEntries(TablePerspective strat, Group group, Object receiver) {
		Collection<AContextMenuItem> items = Lists.newArrayList();
		for (IScoreFactory f : factories.values()) {
			for (ScoreEntry p : f.createGroupEntries(strat, group)) {
				items.add(new GenericContextMenuItem(p.getLabel(), new AddScoreColumnEvent(p.getScores()).to(receiver)));
			}
		}
		return items;
	}

	public static Collection<AContextMenuItem> createStratEntries(TablePerspective strat, Object receiver) {
		Collection<AContextMenuItem> items = Lists.newArrayList();
		for (IScoreFactory f : factories.values()) {
			for (ScoreEntry p : f.createStratEntries(strat)) {
				items.add(new GenericContextMenuItem(p.getLabel(), new AddScoreColumnEvent(p.getScores()).to(receiver)));
			}
		}
		return items;
	}

	public static IScoreFactory get(String score) {
		return factories.get(score);
	}

	public static void fillStateMachine(IStateMachine stateMachine, Object eventReceiver,
			List<TablePerspective> existing) {
		for (IScoreFactory f : factories.values()) {
			f.fillStateMachine(stateMachine, eventReceiver, existing);
		}
	}
}
