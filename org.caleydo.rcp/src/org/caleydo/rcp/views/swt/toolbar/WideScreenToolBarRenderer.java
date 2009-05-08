package org.caleydo.rcp.views.swt.toolbar;

import java.util.List;

import org.caleydo.rcp.action.toolbar.general.ExportDataAction;
import org.caleydo.rcp.action.toolbar.general.LoadDataAction;
import org.caleydo.rcp.action.toolbar.general.OpenSearchViewAction;
import org.caleydo.rcp.action.toolbar.view.ClearSelectionsAction;
import org.caleydo.rcp.action.toolbar.view.TakeSnapshotAction;
import org.caleydo.rcp.views.swt.toolbar.content.AToolBarContent;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;

/**
 * Provides toolbar rendering specific data for widescreen toolbars (toolbars on the left side) 
 * in the default style (toolbar on the left side). 
 * @author Werner Puff
 */
public class WideScreenToolBarRenderer implements IToolBarRenderer {

	@Override
	public Runnable createRenderJob(ToolBarView toolBarView, List<AToolBarContent> toolBarContents) {
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
	public void addGeneralToolBarActions(ToolBarManager toolBarManager, Group group) {
		toolBarManager.add(new LoadDataAction());
		toolBarManager.add(new ExportDataAction());
		toolBarManager.add(new TakeSnapshotAction());
		toolBarManager.add(new OpenSearchViewAction());
		toolBarManager.add(new ClearSelectionsAction());
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