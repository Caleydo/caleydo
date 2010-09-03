package org.caleydo.rcp.perspective;

import org.caleydo.rcp.startup.StartupProcessor;
import org.caleydo.rcp.toolbar.RcpToolBarView;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;
import org.eclipse.ui.PlatformUI;

public class GenomePerspective
	implements IPerspectiveFactory {

	public static boolean bIsWideScreen = false;

	public static IFolderLayout mainViewFolder;

	@Override
	public void createInitialLayout(final IPageLayout layout) {
		
		layout.setEditorAreaVisible(false);

		Rectangle rectDisplay = Display.getCurrent().getPrimaryMonitor().getBounds();
		float fRatio = (float) rectDisplay.width / rectDisplay.height;

		if (fRatio > 1.35) {
			bIsWideScreen = true;
		}
		
		if (bIsWideScreen) {
			fRatio =
				(float) RcpToolBarView.TOOLBAR_WIDTH
					/ rectDisplay.width;

			IFolderLayout topLeft =
				layout.createFolder("topLeft", IPageLayout.LEFT, fRatio, IPageLayout.ID_EDITOR_AREA);
			topLeft.addView(RcpToolBarView.ID);

			IFolderLayout middleLeft = layout.createFolder("middleLeft", IPageLayout.BOTTOM, 0.2f, "topLeft");
			middleLeft.addView("org.caleydo.view.info");
			middleLeft.addPlaceholder("org.caleydo.view.statistics");

			IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.4f, "middleLeft");
			bottomLeft.addView("org.caleydo.view.datameta");

			IFolderLayout mainLayout =
				layout.createFolder("main", IPageLayout.RIGHT, 0.3f,
					IPageLayout.ID_EDITOR_AREA);

			IPlaceholderFolderLayout rightLayout = layout.createPlaceholderFolder("right", IPageLayout.RIGHT, 1-2*fRatio, "main");
			rightLayout.addPlaceholder("org.caleydo.view.bookmark");
			rightLayout.addPlaceholder("org.caleydo.view.grouper");
			
			StartupProcessor.get().getStartupProcedure().openRCPViews(mainLayout);

		}
		else {
			fRatio =
				(float) RcpToolBarView.TOOLBAR_HEIGHT
					/ PlatformUI.getWorkbench().getDisplay().getClientArea().height;

			IFolderLayout topFolder =
				layout.createFolder("top", IPageLayout.TOP, fRatio, IPageLayout.ID_EDITOR_AREA);
			topFolder.addView(RcpToolBarView.ID);

			// if (Application.applicationMode != ApplicationMode.NO_DATA) {

			layout.addStandaloneView("org.caleydo.view.info", true, IPageLayout.RIGHT, 0.75f, "top");
			layout.addStandaloneView("org.caleydo.view.datameta", true, IPageLayout.RIGHT, 0.8f, "top");
			// }
			// IFolderLayout bottomFolder = layout.createFolder("bottom", IPageLayout.BOTTOM, 1 - fRatio,
			// IPageLayout.ID_EDITOR_AREA);

			// layout.addStandaloneView(ToolBarView.ID, false, IPageLayout.TOP, fRatio,
			// IPageLayout.ID_EDITOR_AREA);

			IFolderLayout mainLayout =
				layout.createFolder("folderLayoutRight", IPageLayout.BOTTOM, 1 - fRatio,
					IPageLayout.ID_EDITOR_AREA);

			StartupProcessor.get().getStartupProcedure().openRCPViews(mainLayout);
		}
		// layout.addPlaceholder("org.caleydo.view.grouper", IPageLayout.RIGHT, (float) 0.85,
		// "folderLayoutRight");
	}
}
