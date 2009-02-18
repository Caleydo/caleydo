package org.caleydo.rcp.perspective;

import org.caleydo.rcp.views.swt.ToolBarView;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.PlatformUI;

public class GenomePerspective
	implements IPerspectiveFactory
{
	public static boolean bIsWideScreen = false;
	
	public void createInitialLayout(final IPageLayout layout)
	{
		layout.setEditorAreaVisible(false);
//		layout.setFixed(true);
		layout.getViewLayout("org.caleydo.rcp.views.HTMLBrowserView").setCloseable(false);
		layout.getViewLayout("org.caleydo.rcp.views.ToolBarView").setCloseable(false);
		layout.getViewLayout("org.caleydo.rcp.views.ToolBarView").setMoveable(false);

		Rectangle rectDisplay = Display.getCurrent().getMonitors()[0].getBounds();
		float fRatio = (float)rectDisplay.width / rectDisplay.height;

		if (fRatio > 1.35)
			bIsWideScreen = true;

		if (bIsWideScreen)
		{
			fRatio = (float) ToolBarView.TOOLBAR_WIDTH
				/ PlatformUI.getWorkbench().getDisplay().getMonitors()[0].getBounds().width;			

			layout.addStandaloneView("org.caleydo.rcp.views.ToolBarView", false, IPageLayout.LEFT,
					fRatio, IPageLayout.ID_EDITOR_AREA);
			layout.createFolder("folderLayoutRight", IPageLayout.RIGHT, 1 - fRatio,
					IPageLayout.ID_EDITOR_AREA);
		}
		else
		{
			fRatio = (float) ToolBarView.TOOLBAR_HEIGHT
				/ PlatformUI.getWorkbench().getDisplay().getMonitors()[0].getBounds().height;

			layout.addStandaloneView("org.caleydo.rcp.views.ToolBarView", false, IPageLayout.TOP,
					fRatio, IPageLayout.ID_EDITOR_AREA);
			layout.createFolder("folderLayoutRight", IPageLayout.BOTTOM, 1 - fRatio,
					IPageLayout.ID_EDITOR_AREA);
		}
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(
				new PartListener());
	}
}
