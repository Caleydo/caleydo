package org.caleydo.rcp.views.swt.toolbar;

import java.util.List;

import org.caleydo.rcp.action.toolbar.general.OpenSearchViewAction;
import org.caleydo.rcp.action.toolbar.view.TakeSnapshotAction;
import org.caleydo.rcp.views.swt.toolbar.content.AToolBarContent;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;

/**
 * Provides toolbar rendering specific data for 4:3 screen toolbars (toolbars on upper side of the screen) 
 * in the default style (toolbar on the left side). 
 * @author Werner Puff
 */
public class StandardToolBarRenderer 
	implements IToolBarRenderer {

	public Runnable createRenderJob(ToolBarView toolBarView, List<AToolBarContent> toolBarContents) {
		WideScreenToolBarRenderJob job = new WideScreenToolBarRenderJob();
		job.setToolBarView(toolBarView);
		job.setToolBarContents(toolBarContents);
		job.setToolBarRenderer(this);
		return job;
	}
	
	public GridLayout createLayout() {
		return new GridLayout(10, false);
	}

	public void addTakeSnapshotAction(ToolBarManager toolBarManager, Group group) {
		final ToolBar toolBar2 = new ToolBar(group, SWT.WRAP | SWT.FLAT);
		ToolBarManager toolBarManager2 = new ToolBarManager(toolBar2);
		toolBarManager2.add(new TakeSnapshotAction());
		toolBarManager2.add(new OpenSearchViewAction());
		toolBarManager2.update(true);

		Label spacer = new Label(group, SWT.NULL);
		spacer.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	public int calcWrapCount(int size) {
		int wrapCount;

		if (size <= 4) {
			wrapCount = 2;
		} else {
			wrapCount = 4;
		}

		return wrapCount;
	}

	public GridData createStandardGridData() {
		return new GridData(GridData.FILL_VERTICAL);
	}

}
/*

public GridData createContainerGridData() {
	GridData gridData = new GridData(GridData.FILL_VERTICAL);
	gridData.minimumWidth = 230;
	gridData.widthHint = 230;
	return gridData;
}

public GridLayout createSearchLayout() {
	GridLayout layout = new GridLayout(2, false);
	layout.marginHeight = layout.marginWidth = 0;
	return layout;
}

public GridData createSpacerGridData() {
	return new GridData(GridData.FILL_BOTH);
}

public GridLayout createInfoBarLayout() {
	GridLayout layout = new GridLayout(2, false);
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

public void addInfoBarSpacer(Group group) {
	Label spacer = new Label(group, SWT.NULL);
	spacer.setLayoutData(new GridData(GridData.FILL_BOTH));
}

public GridData createColorMappingGridData() {
	GridData data = new GridData(GridData.FILL_VERTICAL);
	data.minimumWidth = 110;
	data.widthHint = 110;
	return data;
}
*/