package org.caleydo.rcp.views;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.view.rcp.CmdExternalActionTrigger;
import org.caleydo.core.command.view.rcp.EExternalActionType;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.util.search.SearchBar;

public class GLRemoteRendering3DView 
extends AGLViewPart {

	public static final String ID = "org.caleydo.rcp.views.GLRemoteRendering3DView";

	public static final String ACTION_TOGGLE_LAYOUT_MODE_TEXT = "Toggle Jukebox/Bucket";
	public static final String ACTION_TOGGLE_LAYOUT_MODE_ICON = "resources/icons/PathwayEditor/gene_mapping.png";

	protected int iGLCanvasDirectorId;
	
	private Action actToggleLayoutMode;
	
	/**
	 * Constructor.
	 */
	public GLRemoteRendering3DView() {
		
		super();
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {

		super.createPartControlSWT(parent);
		
		createToggleLayoutStyleAction();
		
		contributeToActionBars();
	}
	
	protected void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	protected void fillLocalPullDown(IMenuManager manager) {		

//		manager.add(new Separator());
//		manager.add(actToggleLayoutMode);
	}
	
	protected void fillLocalToolBar(IToolBarManager manager) {

		IContributionItem searchBar = 
			new SearchBar("Quick search");

		manager.add(new Separator());
		manager.add(searchBar);
		manager.add(actToggleLayoutMode);
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

	private void createToggleLayoutStyleAction() {

		actToggleLayoutMode = new Action() {
			public void run() {

				triggerCmdExternalAction(EExternalActionType.REMOTE_RENDONG_TOGGLE_LAYOUT_MODE);
			}
		};

		actToggleLayoutMode.setText(ACTION_TOGGLE_LAYOUT_MODE_TEXT);
		actToggleLayoutMode.setToolTipText(ACTION_TOGGLE_LAYOUT_MODE_TEXT);
		actToggleLayoutMode.setImageDescriptor(ImageDescriptor
				.createFromURL(this.getClass().getClassLoader().getResource(
						ACTION_TOGGLE_LAYOUT_MODE_ICON)));
	}
	
	public void triggerCmdExternalAction(EExternalActionType type) {

		CmdExternalActionTrigger tmpCmd = (CmdExternalActionTrigger) Application.refGeneralManager
				.getSingelton().getCommandManager().createCommandByType(
						CommandQueueSaxType.EXTERNAL_ACTION_TRIGGER);

		// FIXME: hard coded view ID
		tmpCmd.setAttributes(83401, type);
		tmpCmd.doCommand();
	}
}