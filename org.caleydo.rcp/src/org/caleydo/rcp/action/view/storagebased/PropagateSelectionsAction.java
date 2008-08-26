package org.caleydo.rcp.action.view.storagebased;

import org.caleydo.core.command.view.rcp.EExternalActionType;
import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.rcp.action.view.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;


public class PropagateSelectionsAction
extends AToolBarAction
{
	public static final String TEXT = "Propagate selections to other views";
	public static final String ICON = "resources/icons/view/storagebased/propagate_selection.png";

	/**
	 * Constructor.
	 */
	public PropagateSelectionsAction(int iViewID)
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
			
		triggerCmdExternalAction(EExternalActionType.STORAGEBASED_PROPAGATE_SELECTIONS);
	};
}
