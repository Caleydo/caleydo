package org.geneview.rcp;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IViewLayout;
import org.geneview.rcp.views.GLHeatmap2DView;
import org.geneview.rcp.views.GLJukeboxPathwayView;
import org.geneview.rcp.views.browser.IBrowserConstants;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {

		layout.setEditorAreaVisible(false);

		//	    layout.addView(GLJukeboxPathwayView.ID +":1", IPageLayout.LEFT,
		//	        0.5f, layout.getEditorArea());
		//
		//	    layout.addView(GLJukeboxPathwayView.ID +":2", IPageLayout.LEFT,
		//		    0.5f, layout.getEditorArea());

		//	    layout.addStandaloneView(GLJukeboxPathwayView.ID, false,
		//        IPageLayout.LEFT, 1.0f, layout.getEditorArea());

		IFolderLayout folder = layout.createFolder("views", IPageLayout.LEFT,
				1.0f, layout.getEditorArea());
		folder.addPlaceholder(GLJukeboxPathwayView.ID + ":*");
		folder.addView(GLJukeboxPathwayView.ID + ":1");
		folder.addView(GLHeatmap2DView.ID + ":1");
		folder.addView(IBrowserConstants.BROWSER_VIEW_ID);
		//folder.addView(GLJukeboxPathwayView.ID + ":2");
		IViewLayout viewLayout = layout.getViewLayout(GLJukeboxPathwayView.ID);
		viewLayout.setCloseable(false);
	}
}
