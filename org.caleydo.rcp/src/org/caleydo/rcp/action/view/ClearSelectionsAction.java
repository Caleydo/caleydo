package org.caleydo.rcp.action.view;

import org.caleydo.core.command.view.rcp.EExternalActionType;
import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.eclipse.jface.resource.ImageDescriptor;


public class ClearSelectionsAction
extends AToolBarAction
{
	public static final String TEXT = "Clear selections";
	public static final String ICON = "resources/icons/general/clear_selections.png";

	/**
	 * Constructor.
	 */
	public ClearSelectionsAction(int iViewID)
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
			
		triggerCmdExternalAction(EExternalActionType.CLEAR_SELECTIONS);
	};
}
