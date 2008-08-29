package org.caleydo.rcp.action.view.storagebased;

import org.caleydo.core.command.view.rcp.EExternalActionType;
import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.rcp.action.view.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;


public class ChangeOrientationAction
extends AToolBarAction
{
	public static final String TEXT = "Switch dimensions";
	public static final String ICON = "resources/icons/view/storagebased/change_orientation.png";

	private boolean bEnable = false;
	
	/**
	 * Constructor.
	 */
	public ChangeOrientationAction(int iViewID)
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
		bEnable = !bEnable;	
		triggerCmdSExternalFlagSetter(bEnable, EExternalFlagSetterType.STORAGEBASED_CHANGE_ORIENTATION);
	};
}
