package org.caleydo.rcp.perspective;

import org.caleydo.rcp.startup.StartupProcessor;
import org.caleydo.rcp.toolbar.RcpToolBarView;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;

public class GenomePerspective
	implements IPerspectiveFactory {

	public static IFolderLayout mainViewFolder;

	@Override
	public void createInitialLayout(final IPageLayout layout) {

		layout.setEditorAreaVisible(false);

		Rectangle rectDisplay = Display.getCurrent().getPrimaryMonitor().getBounds();
		float fRatio = (float) RcpToolBarView.TOOLBAR_WIDTH / rectDisplay.width;

		IFolderLayout topLeft =
			layout.createFolder("topLeft", IPageLayout.LEFT, fRatio, IPageLayout.ID_EDITOR_AREA);
		topLeft.addView(RcpToolBarView.ID);

		IFolderLayout middleLeft = layout.createFolder("middleLeft", IPageLayout.BOTTOM, 0.2f, "topLeft");
		middleLeft.addView("org.caleydo.view.info");
		// middleLeft.addPlaceholder("org.caleydo.view.statistics");

		IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.4f, "middleLeft");
		bottomLeft.addView("org.caleydo.view.datameta");
		bottomLeft.addPlaceholder("org.caleydo.view.filter");

		IFolderLayout mainLayout =
			layout.createFolder("main", IPageLayout.RIGHT, 0.3f, IPageLayout.ID_EDITOR_AREA);

		IPlaceholderFolderLayout rightLayout =
			layout.createPlaceholderFolder("right", IPageLayout.RIGHT, 1 - 2 * fRatio, "main");
		rightLayout.addPlaceholder("org.caleydo.view.bookmark");
		rightLayout.addPlaceholder("org.caleydo.view.grouper");

		StartupProcessor.get().getStartupProcedure().openRCPViews(mainLayout);
	}
}
