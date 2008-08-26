package org.caleydo.rcp.action.view.remote;

import org.caleydo.core.command.view.rcp.EExternalActionType;
import org.caleydo.rcp.action.view.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;


public class EraseAction
extends AToolBarAction
{
	public static final String TEXT = "Clear all";
	public static final String ICON = "resources/icons/view/remote/eraser.png";
	
	/**
	 * Constructor.
	 */
	public EraseAction(int iViewID)
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
			
		triggerCmdExternalAction(EExternalActionType.CLEAR_ALL);
	};
}
