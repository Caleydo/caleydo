package org.geneview.rcp;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IViewLayout;
import org.geneview.rcp.views.SnippetJFrameView;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {

		layout.setEditorAreaVisible(false);

		//	    layout.addView(SnippetJFrameView.ID +":1", IPageLayout.LEFT,
		//	        0.5f, layout.getEditorArea());
		//
		//	    layout.addView(SnippetJFrameView.ID +":2", IPageLayout.LEFT,
		//		    0.5f, layout.getEditorArea());

		//	    layout.addStandaloneView(SnippetJFrameView.ID, false,
		//        IPageLayout.LEFT, 1.0f, layout.getEditorArea());

		IFolderLayout folder = layout.createFolder("views", IPageLayout.LEFT,
				1.0f, layout.getEditorArea());
		folder.addPlaceholder(SnippetJFrameView.ID + ":*");
		folder.addView(SnippetJFrameView.ID + ":1");
		//folder.addView(SnippetJFrameView.ID + ":2");
		IViewLayout viewLayout = layout.getViewLayout(SnippetJFrameView.ID);
		viewLayout.setCloseable(false);
	}
}
