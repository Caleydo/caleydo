package org.geneview.rcp.views;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

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

public class GLHeatmap2DView 
extends AGLViewPart {

	public static final String ID = "org.geneview.rcp.views.GLHeatmap2DView";
	
	protected Action action1;

	protected int iGLCanvasDirectorId;
	
	/**
	 * The constructor.
	 */
	public GLHeatmap2DView() {
		super();
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {

		super.createPartControlSWT(parent);
		
		createAnimatorToggleAction();
		contributeToActionBars();
	}
	
	protected void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	protected void fillLocalPullDown(IMenuManager manager) {		
		manager.add(action1);
		manager.add(new Separator());
	}
	
	protected void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
	}

	protected void createAnimatorToggleAction() {
		
		//showMessage("Action 1", "make new action [toggle JOGL frame]");
		
		action1 = new Action() {
			public void run() {
								
				if ( swtComposite.isVisible() ) {
					/* toggle state */
					setGLCanvasVisible( ! frameGL.isVisible() );
				} //if ( swtComposite.isVisible() ) {
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
//		showMessage("Action 1","executed toggle JOGL frame");
	}
	
	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
		
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		
	}
}