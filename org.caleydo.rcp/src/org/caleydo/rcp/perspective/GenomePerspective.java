package org.caleydo.rcp.perspective;

import org.caleydo.rcp.Application;
import org.caleydo.rcp.toolbar.RcpToolBarView;
import org.caleydo.view.base.rcp.RcpGLHistogramView;
import org.caleydo.view.base.swt.RcpSelectionInfoView;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.PlatformUI;

public class GenomePerspective
	implements IPerspectiveFactory {
	public static boolean bIsWideScreen = false;

	// private static final String LOG_VIEW = "org.eclipse.pde.runtime.LogView";

	@Override
	public void createInitialLayout(final IPageLayout layout) {

		layout.setEditorAreaVisible(false);

		Rectangle rectDisplay = Display.getCurrent().getMonitors()[0].getBounds();
		float fRatio = (float) rectDisplay.width / rectDisplay.height;

		if (fRatio > 1.35) {
			bIsWideScreen = true;
		}

		if (bIsWideScreen) {
			fRatio =
				(float) RcpToolBarView.TOOLBAR_WIDTH
					/ PlatformUI.getWorkbench().getDisplay().getMonitors()[0].getBounds().width;

			IFolderLayout topLeft =
				layout.createFolder("topLeft", IPageLayout.LEFT, fRatio, IPageLayout.ID_EDITOR_AREA);
			topLeft.addView(RcpToolBarView.ID);

			IFolderLayout middleLeft = layout.createFolder("middleLeft", IPageLayout.BOTTOM, 0.5f, "topLeft");
			middleLeft.addView(RcpSelectionInfoView.ID);

			layout.addStandaloneView(RcpGLHistogramView.ID, true, IPageLayout.BOTTOM, 0.7f, "middleLeft");
			// IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.45f,
			// "middleLeft");
			// bottomLeft.addPlaceholder(GLHistogramView.ID);

			IFolderLayout mainLayout =
				layout.createFolder("folderLayoutRight", IPageLayout.RIGHT, 1 - fRatio,
					IPageLayout.ID_EDITOR_AREA);

			Application.openRCPViews(mainLayout);

		}
		else {
			fRatio =
				(float) RcpToolBarView.TOOLBAR_HEIGHT
					/ PlatformUI.getWorkbench().getDisplay().getMonitors()[0].getBounds().height;

			IFolderLayout topFolder =
				layout.createFolder("top", IPageLayout.TOP, fRatio, IPageLayout.ID_EDITOR_AREA);
			topFolder.addView(RcpToolBarView.ID);

			layout.addStandaloneView(RcpSelectionInfoView.ID, true, IPageLayout.RIGHT, 0.75f, "top");
			layout.addStandaloneView(RcpGLHistogramView.ID, true, IPageLayout.RIGHT, 0.8f, "top");

			// IFolderLayout bottomFolder = layout.createFolder("bottom", IPageLayout.BOTTOM, 1 - fRatio,
			// IPageLayout.ID_EDITOR_AREA);

			// layout.addStandaloneView(ToolBarView.ID, false, IPageLayout.TOP, fRatio,
			// IPageLayout.ID_EDITOR_AREA);

			IFolderLayout mainLayout =
				layout.createFolder("folderLayoutRight", IPageLayout.BOTTOM, 1 - fRatio,
					IPageLayout.ID_EDITOR_AREA);

			Application.openRCPViews(mainLayout);
		}

		// layout.addPlaceholder(LOG_VIEW, IPageLayout.BOTTOM, (float) 0.8, "right");
	}
}
