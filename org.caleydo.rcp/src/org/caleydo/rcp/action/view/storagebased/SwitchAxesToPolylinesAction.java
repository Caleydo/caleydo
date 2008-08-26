package org.caleydo.rcp.action.view.storagebased;

import org.caleydo.core.command.view.rcp.EExternalActionType;
import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.rcp.action.view.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;


public class SwitchAxesToPolylinesAction
extends AToolBarAction
{
	public static final String TEXT = "Switch dimensions";
	public static final String ICON = "resources/icons/view/storagebased/axes_as_polylines.png";

	/**
	 * Constructor.
	 */
	public SwitchAxesToPolylinesAction(int iViewID)
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
			
		triggerCmdExternalAction(EExternalActionType.STORAGEBASED_SWITCH_AXES_TO_POLYLINES);
	};
}
