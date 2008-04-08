package org.geneview.rcp;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.geneview.rcp.views.SnippetJFrameView;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		
	    layout.setEditorAreaVisible(false);
	    layout.addView(SnippetJFrameView.ID +":1", IPageLayout.LEFT,
	        0.7f, layout.getEditorArea());

	    layout.addView(SnippetJFrameView.ID +":2", IPageLayout.RIGHT,
		    0.3f, layout.getEditorArea());
	    
//	    layout.addStandaloneView(SnippetJFrameView.ID, false,
//	            IPageLayout.LEFT, 1.0f, layout.getEditorArea());
	}
}
