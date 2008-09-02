package org.caleydo.rcp.action.view.remote;

import org.caleydo.core.command.view.rcp.EExternalActionType;
import org.caleydo.rcp.action.view.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;


public class CloseOrResetContainedViews
extends AToolBarAction
{
	public static final String TEXT = "Remove Pathways, Reset Other views";
	public static final String ICON = "resources/icons/general/clear_selections.png";

	/**
	 * Constructor.
	 */
	public CloseOrResetContainedViews(int iViewID)
	{
		super(iViewID);
		
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromURL(this.getClass()
				.getClassLoader().getResource(ICON)));
	}
	
	@Override
	public void run()
	{
		super.run();
			
		triggerCmdExternalAction(EExternalActionType.CLOSE_OR_RESET_CONTAINED_VIEWS);
	};
}
