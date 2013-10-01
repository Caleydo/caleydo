/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.score;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.view.tourguide.api.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.api.state.EWizardMode;
import org.caleydo.view.tourguide.api.state.IStateMachine;
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

	public static void fillStateMachine(IStateMachine stateMachine, List<TablePerspective> existing,
 EWizardMode mode,
			TablePerspective source) {
		for (IScoreFactory f : factories.values()) {
			f.fillStateMachine(stateMachine, existing, mode, source);
		}
	}
}
