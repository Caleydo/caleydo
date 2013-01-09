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
import java.util.Map;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.util.ExtensionUtils;
import org.caleydo.view.tourguide.internal.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.internal.event.CreateScoreEvent;
import org.caleydo.view.tourguide.internal.view.ScoreQueryUI;
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

	public static void addCreateItems(ContextMenuCreator creator, ScoreQueryUI sender, EDataDomainQueryMode mode) {
		for (Map.Entry<String, IScoreFactory> entry : factories.entrySet()) {
			if (entry.getValue().supports(mode))
				creator.addContextMenuItem(new GenericContextMenuItem("Create " + entry.getKey(), new CreateScoreEvent(
						entry.getKey(), sender)));
		}
	}

	public static Iterable<AContextMenuItem> createGroupEntries(TablePerspective strat, Group group) {
		Collection<AContextMenuItem> items = Lists.newArrayList();
		for (IScoreFactory f : factories.values()) {
			for (ScoreEntry p : f.createGroupEntries(strat, group)) {
				items.add(new GenericContextMenuItem(p.getLabel(), new AddScoreColumnEvent(p.getScores(), null)));
			}
		}
		return items;
	}

	public static Collection<AContextMenuItem> createStratEntries(TablePerspective strat) {
		Collection<AContextMenuItem> items = Lists.newArrayList();
		for (IScoreFactory f : factories.values()) {
			for (ScoreEntry p : f.createStratEntries(strat)) {
				items.add(new GenericContextMenuItem(p.getLabel(), new AddScoreColumnEvent(p.getScores(), null)));
			}
		}
		return items;
	}

	public static IScoreFactory get(String score) {
		return factories.get(score);
	}
}
