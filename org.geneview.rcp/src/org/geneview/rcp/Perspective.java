package org.geneview.rcp;

import javax.swing.text.html.HTML;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IViewLayout;
import org.geneview.rcp.views.GLHeatmap2DView;
import org.geneview.rcp.views.GLHeatmap2DView_second;
import org.geneview.rcp.views.GLHeatmap2DView_third;
import org.geneview.rcp.views.GLJukeboxPathwayView;
import org.geneview.rcp.views.HTMLBrowserView;
import org.geneview.rcp.views.Pathway2DView;
import org.geneview.rcp.views.browser.IBrowserConstants;

import org.geneview.core.view.swt.browser.HTMLBrowserViewRep;

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
		folder.addView(GLHeatmap2DView_second.ID + ":1");		
		folder.addView(GLHeatmap2DView_third.ID + ":1");
		folder.addView(Pathway2DView.ID + ":1");
		folder.addView(HTMLBrowserView.ID + ":1");
		//folder.addView(IBrowserConstants.BROWSER_VIEW_ID);
		//folder.addView(GLJukeboxPathwayView.ID + ":2");
		IViewLayout viewLayout = layout.getViewLayout(GLJukeboxPathwayView.ID);
		viewLayout.setCloseable(false);
	}
}
