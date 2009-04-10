package org.caleydo.rcp.views.swt.toolbar;

import java.util.List;

import org.caleydo.rcp.action.toolbar.general.OpenSearchViewAction;
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
		WideScreenToolBarRenderJob job = new WideScreenToolBarRenderJob();
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
	public void addTakeSnapshotAction(ToolBarManager toolBarManager, Group group) {
		toolBarManager.add(new TakeSnapshotAction());
		toolBarManager.add(new OpenSearchViewAction());
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
/*
	@Override
	public GridData createContainerGridData() {
		return new GridData(GridData.FILL_HORIZONTAL);
	}

	@Override
	public GridLayout createSearchLayout() {
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = layout.marginWidth = 0;
		return layout;
	}

	@Override
	public GridData createSpacerGridData() {
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.minimumHeight = 10;
		data.heightHint = 10;
		return data;
	}
	@Override
	public GridLayout createInfoBarLayout() {
		GridLayout layout = new GridLayout(1, false);
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0; 
		layout.marginWidth = 0;

		return layout;
	}

	@Override
	public void addInfoBarSpacer(Group group) {
		// no spacer for vertical toolbars
	}

	@Override
	public GridData createColorMappingGridData() {
		return new GridData(GridData.FILL_HORIZONTAL);
	}

*/