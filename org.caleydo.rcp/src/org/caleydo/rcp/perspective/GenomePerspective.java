package org.caleydo.rcp.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class GenomePerspective
	implements IPerspectiveFactory
{

	public void createInitialLayout(IPageLayout layout)
	{

		layout.setEditorAreaVisible(false);
		layout.getViewLayout("org.caleydo.rcp.views.HTMLBrowserView").setCloseable(false);

		// layout.addView(GLJukeboxPathwayView.ID +":1", IPageLayout.LEFT,
		// 0.5f, layout.getEditorArea());
		//
		// layout.addView(GLJukeboxPathwayView.ID +":2", IPageLayout.LEFT,
		// 0.5f, layout.getEditorArea());

		// layout.addStandaloneView(GLJukeboxPathwayView.ID, false,
		// IPageLayout.LEFT, 1.0f, layout.getEditorArea());

		// IFolderLayout folder = layout.createFolder("views", IPageLayout.LEFT,
		// 1.0f, layout
		// .getEditorArea());
		//		
		// ArrayList<EStartViewsMode> alStartViews = Application.alStartViews;
		// if (alStartViews.contains(EStartViewsMode.REMOTE))
		// {
		// folder.addView(GLRemoteRenderingView.ID + ":1");
		// alStartViews.remove(EStartViewsMode.REMOTE);
		// }
		//		
		// for (EStartViewsMode startViewsMode : alStartViews)
		// {
		// folder.addView(startViewsMode.getRCPViewID() + ":1");
		// }

		// IViewLayout viewLayout =
		// layout.getViewLayout(GLRemoteRenderingView.ID);
		// viewLayout.setCloseable(false);
	}
}
