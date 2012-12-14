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
package org.caleydo.view.tourguide.contextmenu;

import static org.caleydo.view.tourguide.event.EScoreReferenceMode.ALL_GROUPS_IN_COLUMN;
import static org.caleydo.view.tourguide.event.EScoreReferenceMode.COLUMN;
import static org.caleydo.view.tourguide.event.EScoreReferenceMode.MUTUAL_EXCLUSIVE_GROUP;
import static org.caleydo.view.tourguide.event.EScoreReferenceMode.SINGLE_GROUP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.gui.util.DisplayUtils;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.execution.SafeCallable;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.contextmenu.GroupContextMenuItem;
import org.caleydo.view.stratomex.brick.IContextMenuBrickFactory;
import org.caleydo.view.stratomex.column.BrickColumn;
import org.caleydo.view.tourguide.event.EScoreReferenceMode;
import org.caleydo.view.tourguide.event.LogRankScoreTablePerspectiveEvent;
import org.caleydo.view.tourguide.event.ScoreTablePerspectiveEvent;
import org.caleydo.view.tourguide.vendingmachine.VendingMachine;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Samuel Gratzl
 *
 */
public class ScoreContextMenuBrickFactory implements IContextMenuBrickFactory {

	@Override
	public Iterable<AContextMenuItem> createGroupEntries(BrickColumn brick, TablePerspective group) {
		if (!isTourGuideVisible()) // show context menu only if the tour guide view is visible
			return Collections.emptyList();
		Collection<AContextMenuItem> col = new ArrayList<>();
		col.add(create("Score group", SINGLE_GROUP, brick, group));
		col.add(create("Score group (Mutual Exclusive)", MUTUAL_EXCLUSIVE_GROUP, brick, group));
		col.add(createLogRank(brick, group));
		return col;
	}


	@Override
	public Iterable<AContextMenuItem> createStratification(BrickColumn brick) {
		if (!isTourGuideVisible())
			return Collections.emptyList();
		Collection<AContextMenuItem> col = new ArrayList<>();
		col.add(create("Score column", COLUMN, brick, null));
		col.add(create("Score all groups in column", ALL_GROUPS_IN_COLUMN, brick, null));
		col.add(createLogRank(brick, null));
		return col;
	}

	private AContextMenuItem createLogRank(BrickColumn brick, TablePerspective group) {
		TablePerspective strat = brick.getTablePerspective();
		EScoreReferenceMode mode = group == null ? ALL_GROUPS_IN_COLUMN: SINGLE_GROUP;
		GroupContextMenuItem g = new GroupContextMenuItem("Log Rank Score of ");
		for(Pair<Integer,String> pair : DataDomainOracle.getClinicalVariables()) {
			g.add(new GenericContextMenuItem(pair.getSecond(), new LogRankScoreTablePerspectiveEvent(pair.getFirst(),
					mode, strat, group)));
		}
		return g;
	}


	private static AContextMenuItem create(String label, EScoreReferenceMode mode, BrickColumn brick,
			TablePerspective group) {
		return new GenericContextMenuItem(label, new ScoreTablePerspectiveEvent(mode, brick.getTablePerspective(),
				group));
	}

	private static boolean isTourGuideVisible() {
		final IWorkbench wb = PlatformUI.getWorkbench();
		return DisplayUtils.syncExec(wb.getDisplay(), new SafeCallable<Boolean>() {
			@Override
			public Boolean call() {
				IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
				IWorkbenchPage page = win.getActivePage();
				return page.findViewReference(VendingMachine.VIEW_TYPE) != null;
			}
		}).booleanValue();
	}

}
