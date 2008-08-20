package org.caleydo.rcp.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class GLHeatMapView
	extends AGLViewPart
{

	public static final String ID = "org.caleydo.rcp.views.GLHeatmap2DView";

	protected Action action1;

	protected int iGLCanvasDirectorId;

	/**
	 * Constructor.
	 */
	public GLHeatMapView()
	{
		super();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{

		super.createPartControlSWT(parent);

		createAnimatorToggleAction();
		contributeToActionBars();
	}

	protected void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	protected void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(action1);
		manager.add(new Separator());
	}

	protected void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(action1);
	}

	protected void createAnimatorToggleAction()
	{

		action1 = new Action()
		{
			public void run()
			{

				if (swtComposite.isVisible())
				{
					/* toggle state */
					// setGLCanvasVisible( ! frameGL.isVisible() );
				} // if ( swtComposite.isVisible() ) {
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}
}