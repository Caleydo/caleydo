package org.geneview.rcp.views;


import org.geneview.rcp.views.GLHeatmap2DView;

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class GLHeatmap2DView_third extends GLHeatmap2DView {

	public static final String ID = "org.geneview.rcp.views.GLHeatmap2DView_third";
	
	/**
	 * The constructor.
	 */
	public GLHeatmap2DView_third() {
		super();
		
		//TODO: remove hack for GLCanvasdirector
		iGLCanvasDirectorId = 29;
	}
}