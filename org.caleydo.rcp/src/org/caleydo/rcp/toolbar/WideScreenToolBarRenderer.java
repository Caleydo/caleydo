package org.caleydo.rcp.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.manager.datadomain.EDataDomain;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.rcp.action.toolbar.general.ExportDataAction;
import org.caleydo.rcp.action.toolbar.general.ImportDataAction;
import org.caleydo.rcp.action.toolbar.general.OpenSearchViewAction;
import org.caleydo.rcp.action.toolbar.general.RestoreOriginalDataAction;
import org.caleydo.rcp.action.toolbar.general.SaveProjectAction;
import org.caleydo.rcp.action.toolbar.view.ClearSelectionsAction;
import org.caleydo.rcp.action.toolbar.view.StartClusteringAction;
import org.caleydo.rcp.action.toolbar.view.TakeSnapshotAction;
import org.caleydo.rcp.view.toolbar.content.AToolBarContent;
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

		// IToolBarItem startClustering = new StartClusteringAction(targetViewID);
		// actionList.add(startClustering);

		if (GeneralManager.get().getUseCase(EDataDomain.GENETIC_DATA) != null) {
			toolBarManager2.add(new OpenSearchViewAction());
		}

		toolBarManager2.add(new ClearSelectionsAction());
		toolBarManager2.add(new StartClusteringAction());
		toolBarManager2.add(new RestoreOriginalDataAction());

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