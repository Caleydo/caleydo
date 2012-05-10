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
package org.caleydo.core.gui.toolbar;

import java.util.ArrayList;
import java.util.List;
import org.caleydo.core.gui.toolbar.action.ClearSelectionsAction;
import org.caleydo.core.gui.toolbar.action.SaveProjectAction;
import org.caleydo.core.gui.toolbar.action.StartClusteringAction;
import org.caleydo.core.gui.toolbar.action.TakeSnapshotAction;
import org.caleydo.core.io.gui.ExportDataAction;
import org.caleydo.core.io.gui.ImportDataAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ToolBar;

/**
 * Provides toolbar rendering specific data for widescreen toolbars (toolbars on the left side) in the default
 * style (toolbar on the left side).
 * 
 * @author Werner Puff
 */
public class WideScreenToolBarRenderer
	implements IToolBarRenderer {

	@Override
	public Runnable createRenderJob(RcpToolBarView toolBarView, List<AToolBarContent> toolBarContents) {
		DefaultToolBarRenderJob job = new DefaultToolBarRenderJob();
		job.setToolBarView(toolBarView);
		job.setToolBarContents(toolBarContents);
		job.setToolBarRenderer(this);
		return job;
	}

	@Override
	public GridLayout createLayout() {
		return new GridLayout(1, false);
	}

	@Override
	public void addGeneralToolBarActions(Group group) {

		// Needed to simulate toolbar wrapping which is not implemented for
		// linux
		// See bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=46025
		ArrayList<ToolBar> alToolBar = new ArrayList<ToolBar>();
		ArrayList<IToolBarManager> alToolBarManager = new ArrayList<IToolBarManager>();

		final ToolBar toolBar = new ToolBar(group, SWT.WRAP | SWT.FLAT);
		ToolBarManager toolBarManager = new ToolBarManager(toolBar);
		alToolBar.add(toolBar);
		alToolBarManager.add(toolBarManager);

		final ToolBar toolBar2 = new ToolBar(group, SWT.WRAP | SWT.FLAT);
		ToolBarManager toolBarManager2 = new ToolBarManager(toolBar2);
		alToolBar.add(toolBar2);
		alToolBarManager.add(toolBarManager2);

		toolBarManager.add(new SaveProjectAction());
		toolBarManager.add(new ImportDataAction());
		toolBarManager.add(new ExportDataAction());
		toolBarManager.add(new TakeSnapshotAction());

		// IToolBarItem startClustering = new StartClusteringDialogAction(targetViewID);
		// actionList.add(startClustering);

		// if (DataDomainManager.getInstance().getDataDomain("org.caleydo.datadomain.genetic") != null) {
		// toolBarManager2.add(new OpenSearchViewAction());
		// }

		toolBarManager2.add(new ClearSelectionsAction());
		toolBarManager2.add(new StartClusteringAction());
		// FIXME: removed because we need new concept for restoring data
		// toolBarManager2.add(new RestoreOriginalDataAction());

//		toolBarManager2.add(new SwitchDataRepresentationAction());

		toolBarManager.update(true);

		if (toolBarManager2.isEmpty())
			toolBarManager2.dispose();
		else
			toolBarManager2.update(true);
	}

	@Override
	public GridData createStandardGridData() {
		return new GridData(GridData.FILL_HORIZONTAL);
	}

	@Override
	public int calcWrapCount(int size) {
		return 4;
	}
}