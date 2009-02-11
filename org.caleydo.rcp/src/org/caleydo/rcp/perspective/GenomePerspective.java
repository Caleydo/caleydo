package org.caleydo.rcp.perspective;

import org.caleydo.rcp.views.swt.ToolBarView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.PlatformUI;

public class GenomePerspective
	implements IPerspectiveFactory
{

	public void createInitialLayout(final IPageLayout layout)
	{
		layout.setEditorAreaVisible(false);
//		layout.setFixed(true);
		layout.getViewLayout("org.caleydo.rcp.views.HTMLBrowserView").setCloseable(false);
		layout.getViewLayout("org.caleydo.rcp.views.ToolBarView").setCloseable(false);
		layout.getViewLayout("org.caleydo.rcp.views.ToolBarView").setMoveable(false);

		float fRatio = (float) ToolBarView.TOOLBAR_WIDTH * 1.25f
				/ PlatformUI.getWorkbench().getDisplay().getMonitors()[0].getBounds().width;

		layout.addStandaloneView("org.caleydo.rcp.views.ToolBarView", false, IPageLayout.LEFT,
				fRatio, IPageLayout.ID_EDITOR_AREA);
		layout.createFolder("folderLayoutRight", IPageLayout.RIGHT, 1 - fRatio,
				IPageLayout.ID_EDITOR_AREA);

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(
				new PartListener());
	}
}
