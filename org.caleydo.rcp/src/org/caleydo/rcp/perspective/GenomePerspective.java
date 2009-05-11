package org.caleydo.rcp.perspective;

import org.caleydo.rcp.views.opengl.GLHistogramView;
import org.caleydo.rcp.views.swt.SelectionInfoView;
import org.caleydo.rcp.views.swt.toolbar.ToolBarView;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.PlatformUI;

public class GenomePerspective
	implements IPerspectiveFactory {
	public static boolean bIsWideScreen = false;
	
	private static final String LOG_VIEW = "org.eclipse.pde.runtime.LogView";

	public void createInitialLayout(final IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		// layout.setFixed(true);
		layout.getViewLayout("org.caleydo.rcp.views.HTMLBrowserView").setCloseable(false);
		layout.getViewLayout("org.caleydo.rcp.views.ToolBarView").setCloseable(false);
		layout.getViewLayout("org.caleydo.rcp.views.ToolBarView").setMoveable(false);

		Rectangle rectDisplay = Display.getCurrent().getMonitors()[0].getBounds();
		float fRatio = (float) rectDisplay.width / rectDisplay.height;

		if (fRatio > 1.35) {
			bIsWideScreen = true;
		}

		if (bIsWideScreen) {
			fRatio =
				(float) ToolBarView.TOOLBAR_WIDTH
					/ PlatformUI.getWorkbench().getDisplay().getMonitors()[0].getBounds().width;

			IFolderLayout topLeft =
				layout.createFolder("topLeft", IPageLayout.LEFT, fRatio, IPageLayout.ID_EDITOR_AREA);
			topLeft.addView(ToolBarView.ID);

			IFolderLayout middleLeft =
				layout.createFolder("middleLeft", IPageLayout.BOTTOM, 0.5f, "topLeft");
			middleLeft.addView(SelectionInfoView.ID);			
			
			layout.addStandaloneViewPlaceholder(GLHistogramView.ID, IPageLayout.BOTTOM, 0.7f, "middleLeft", true);
//			IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.45f, "middleLeft");
//			bottomLeft.addPlaceholder(GLHistogramView.ID);

			layout.createFolder("folderLayoutRight", IPageLayout.RIGHT, 1 - fRatio,
				IPageLayout.ID_EDITOR_AREA);

		}
		else {
			fRatio =
				(float) ToolBarView.TOOLBAR_HEIGHT
					/ PlatformUI.getWorkbench().getDisplay().getMonitors()[0].getBounds().height;
			
			IFolderLayout topFolder =
				layout.createFolder("top", IPageLayout.TOP, fRatio, IPageLayout.ID_EDITOR_AREA);
			topFolder.addView(ToolBarView.ID);

			layout.addStandaloneView(SelectionInfoView.ID, true, IPageLayout.RIGHT, 0.75f, "top");
			layout.addStandaloneViewPlaceholder(GLHistogramView.ID, IPageLayout.RIGHT, 0.8f, "top", true);
					
//			IFolderLayout bottomFolder = layout.createFolder("bottom", IPageLayout.BOTTOM, 1 - fRatio, IPageLayout.ID_EDITOR_AREA);		
			
//			layout.addStandaloneView(ToolBarView.ID, false, IPageLayout.TOP, fRatio,
//				IPageLayout.ID_EDITOR_AREA);
			
			layout.createFolder("folderLayoutRight", IPageLayout.BOTTOM, 1 - fRatio,
				IPageLayout.ID_EDITOR_AREA);
		}

//		layout.addPlaceholder(LOG_VIEW,IPageLayout.BOTTOM, (float) 0.8, "right");
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(
			new PartListener());
	}
}
