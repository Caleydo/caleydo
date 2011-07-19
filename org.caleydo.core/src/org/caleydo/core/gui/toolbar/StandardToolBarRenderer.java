package org.caleydo.core.gui.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.gui.toolbar.action.ClearSelectionsAction;
import org.caleydo.core.gui.toolbar.action.RestoreOriginalDataAction;
import org.caleydo.core.gui.toolbar.action.SaveProjectAction;
import org.caleydo.core.gui.toolbar.action.StartClusteringAction;
import org.caleydo.core.gui.toolbar.action.TakeSnapshotAction;
import org.caleydo.core.gui.toolbar.content.AToolBarContent;
import org.caleydo.core.io.gui.ExportDataAction;
import org.caleydo.core.io.gui.ImportDataAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;

/**
 * Provides toolbar rendering specific data for 4:3 screen toolbars (toolbars on upper side of the screen) in
 * the default style (toolbar on the left side).
 * 
 * @author Werner Puff
 */
public class StandardToolBarRenderer
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
		return new GridLayout(10, false);
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

		toolBarManager2.add(new StartClusteringAction());
		// toolBarManager2.add(new OpenSearchViewAction());
		toolBarManager2.add(new ClearSelectionsAction());
		toolBarManager2.add(new RestoreOriginalDataAction());

		// toolBarManager2.add(new MagnifyingGlassAction());

		toolBarManager.update(true);

		if (toolBarManager2.isEmpty())
			toolBarManager2.dispose();
		else
			toolBarManager2.update(true);

		Label spacer = new Label(group, SWT.NULL);
		spacer.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	@Override
	public int calcWrapCount(int size) {
		int wrapCount;

		if (size <= 4) {
			wrapCount = 2;
		}
		else {
			wrapCount = 4;
		}

		return wrapCount;
	}

	@Override
	public GridData createStandardGridData() {
		return new GridData(GridData.FILL_VERTICAL);
	}
}